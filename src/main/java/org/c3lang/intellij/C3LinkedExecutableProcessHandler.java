package org.c3lang.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class C3LinkedExecutableProcessHandler extends ProcessHandler
{
	private static final Pattern LINKED_EXECUTABLE_PATTERN =
			Pattern.compile("Program linked to executable ['\"]([^'\"]+)['\"]\\.");

	private final @NotNull GeneralCommandLine buildCommandLine;
	private final @NotNull String workingDirectory;
	private final @NotNull String executableArguments;
	private final boolean runAfterBuild;
	private final @NotNull StringBuilder outputBuffer = new StringBuilder();
	private @Nullable ProcessHandler activeHandler;
	private boolean stopping;

	C3LinkedExecutableProcessHandler(
			@NotNull GeneralCommandLine buildCommandLine,
			@NotNull String workingDirectory,
			@NotNull String executableArguments,
			boolean runAfterBuild)
	{
		this.buildCommandLine = buildCommandLine;
		this.workingDirectory = workingDirectory;
		this.executableArguments = executableArguments;
		this.runAfterBuild = runAfterBuild;
	}

	@Override
	public void startNotify()
	{
		super.startNotify();
		try
		{
			ProcessHandler buildHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(buildCommandLine);
			activeHandler = buildHandler;
			buildHandler.addProcessListener(new ProcessListener()
			{
				@Override
				public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType)
				{
					relayOutput(event.getText(), outputType);
					outputBuffer.append(event.getText());
				}

				@Override
				public void processTerminated(@NotNull ProcessEvent event)
				{
					handleBuildTerminated(event.getExitCode());
				}
			});
			buildHandler.startNotify();
		}
		catch (ExecutionException e)
		{
			notifyTextAvailable(e.getMessage() + "\n", com.intellij.execution.process.ProcessOutputTypes.STDERR);
			notifyProcessTerminated(1);
		}
	}

	private void handleBuildTerminated(int exitCode)
	{
		if (stopping || exitCode != 0 || !runAfterBuild)
		{
			notifyProcessTerminated(exitCode);
			return;
		}

		String linkedExecutablePath = parseLinkedExecutable(outputBuffer.toString());
		if (linkedExecutablePath.isBlank())
		{
			notifyTextAvailable(
					"Unable to run executable: compiler output did not contain a linked executable path.\n",
					com.intellij.execution.process.ProcessOutputTypes.STDERR);
			notifyProcessTerminated(1);
			return;
		}

		startExecutable(resolveExecutablePath(linkedExecutablePath));
	}

	private void startExecutable(@NotNull String executablePath)
	{
		try
		{
			GeneralCommandLine commandLine = new GeneralCommandLine(executablePath);
			commandLine.addParameters(splitArguments(executableArguments));
			commandLine.setWorkDirectory(workingDirectory);

			ProcessHandler executableHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
			activeHandler = executableHandler;
			executableHandler.addProcessListener(new ProcessListener()
			{
				@Override
				public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType)
				{
					relayOutput(event.getText(), outputType);
				}

				@Override
				public void processTerminated(@NotNull ProcessEvent event)
				{
					notifyProcessTerminated(event.getExitCode());
				}
			});
			executableHandler.startNotify();
		}
		catch (ExecutionException e)
		{
			notifyTextAvailable(e.getMessage() + "\n", com.intellij.execution.process.ProcessOutputTypes.STDERR);
			notifyProcessTerminated(1);
		}
	}

	private @NotNull String resolveExecutablePath(@NotNull String executablePath)
	{
		Path path = Path.of(executablePath);
		if (path.isAbsolute())
		{
			return path.toString();
		}
		return Path.of(workingDirectory).resolve(path).normalize().toString();
	}

	private static @NotNull List<String> splitArguments(@NotNull String arguments)
	{
		String trimmedArguments = arguments.trim();
		if (trimmedArguments.isEmpty())
		{
			return List.of();
		}

		String[] parts = trimmedArguments.split("\\s+");
		List<String> result = new ArrayList<>();
		for (String part : parts)
		{
			if (!part.isBlank()) result.add(part);
		}
		return result;
	}

	private void relayOutput(@NotNull String text, @NotNull Key outputType)
	{
		notifyTextAvailable(text, outputType);
	}

	static @NotNull String parseLinkedExecutable(@NotNull String text)
	{
		String executablePath = "";
		Matcher matcher = LINKED_EXECUTABLE_PATTERN.matcher(text);
		while (matcher.find())
		{
			executablePath = matcher.group(1);
		}
		return executablePath;
	}

	@Override
	protected void destroyProcessImpl()
	{
		stopping = true;
		ProcessHandler handler = activeHandler;
		if (handler != null)
		{
			handler.destroyProcess();
			return;
		}
		notifyProcessTerminated(1);
	}

	@Override
	protected void detachProcessImpl()
	{
		stopping = true;
		ProcessHandler handler = activeHandler;
		if (handler != null)
		{
			handler.detachProcess();
			return;
		}
		notifyProcessDetached();
	}

	@Override
	public boolean detachIsDefault()
	{
		return false;
	}

	@Override
	public @Nullable OutputStream getProcessInput()
	{
		ProcessHandler handler = activeHandler;
		return handler == null ? null : handler.getProcessInput();
	}
}
