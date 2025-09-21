<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# C3IntelliJ Changelog

## [Unreleased]
- Support ???:
- Support foo = ...
- Support trailing , in parameter list.
- Support unary !!.
- Support #! shebang comment

## [0.1.7] - 2025-08-05

- Fixes to `$Type = <expr>` and `alias module`

## [0.1.6] - 2025-08-01

- Support 1LL. Remove support for 1u64.
- Support const enums.
- Support inline enum types.
- Support `$Type = <expr>` works.
- Support `@operator(+)`.
- Support `alias foo = module abc`.

## [0.1.5] - 2025-04-25

- Fixes $Type in params.

## [0.1.4] - 2025-04-24

### Fixes
- Fixed doc comments not finding macro parameters
- Fixed doc comment completions
- Fixed IDE error caused by doc comment not finding underlying function or macro
- Fixed doc comment not finding underlying function or macro when in default module
- Fixed top level code completion interfering with doc comments
- Fixed hover doc displaying parameters that only existed in the doc comment

### Additions
- Added hover doc for macros
- Added doc comment description to hover doc
- Added inspection for missing imports
- Added inspection for missing functions or macro in imports
- Added stdlib path selector in both project wizards
- Added banner that shows if the stdlib path couldn't be detected
- Added string highlighting in doc comments for all strings
- Added settings page
- Added styled parameter names in doc comments

### Optimizations
- Optimized stdlib path lookup by the IDE
- Optimized code completion by implementing new completion system

## [0.1.3] - 2025-04-16

- Documentation on hover.
- Correctly highlight overloads.

## [0.1.2] - 2025-04-04

- Line marker for main.
- Color selector for #ff00aa.

## [0.1.1] - 2025-04-04

- Added new file action for c3 projects
- Fixed project.json target always being called test -> now is the project name

## [0.1.0] - 2025-04-02

- Full 0.7.0 support: faultdef, typedef etc.
- Find usages and rename.
- Create project wizard.
- Color configuration for `return FAULT?;` 

## [0.0.26] - 2025-03-15

- Support for typedef, attrdef, faultdef. 
- Fixes issues with `@pool() =>`, `$exec` and `$Type = int`.

## [0.0.25] - 2025-03-13

Please Use 0.0.24 and 0.0.23 for 0.6.8 and earlier.

- Update `$foreach` and `if` syntax with the changes from 0.7.0.
- Update to new `int?` syntax.
- Update to new fault definitions.
- `alias` replaces `def`.

## [0.0.24] - 2025-03-09

- Code completion for struct, struct fields, union, enum, const, import, functions, macros
- Goto declaration for struct, struct fields, union, enum, const, functions, macros
- Rename identifiers
- Add import QuickFix
- Updated with Foo{int} generic syntax. Removed {| |} and (< >).
- Removed $varef and & arguments for macros. 

## [0.0.23] - 2025-01-28

- Function and import completion
- Support experimental <[]> syntax.

## [0.0.22] - 2025-01-20

- More IntelliJ compatibility updates.

## [0.0.21] - 2025-01-16

- IntelliJ compatibility

## [0.0.20] - 2025-01-03

- Working "run" profiles.

## [0.0.19] - 2024-12-30

- Fix `?!!` syntax. 

## [0.0.18] - 2024-12-15

- Allow for experimental array syntax.
- Remove use of deprecated functions.

## [0.0.17] - 2024-10-09

- Support `<* *>` doc comments.
- Fix syntax for `-` in asm blocks.
- Fix syntax for bytes blocks.

## [0.0.16] - 2024-09-05

- Added `+++`, `&&&`, `|||` support.
- Added support for new named parameters.
- Updated $va-expression syntax.
- Removed deprecated `$or` `$and` `$concat` `$append`.

## [0.0.15] - 2024-07-03

- Added `$concat` and `$append` support.
- Support `{ .foo, .bar }` bitstruct initialization.
- Support `defer (catch err)`
- Support of `213L`

## [0.0.14] - 2024-06-12

### Updated

- 0.6.0 compatibility: Support new syntax for enums.

## [0.0.13] - 2023-10-25

### Updated

- Support `$feature`, `$is_const`, `$and`, `$or`.
- Support `asm` attributes.
- Support new `$defined`.
- Support `interface`.
- Remove support for `$checks`.

## [0.0.12] - 2023-07-24

### Updated

- Remove assert(try ...)
- Support `nextcase default`.

## [0.0.11] - 2023-07-06

### Updated

- Support new generics syntax.

## [0.0.10] - 2023-07-02

### Fixed

- Fixed incorrect parsing of integer generics.

## [0.0.9] - 2023-06-24

### Updated
- `assert` now accepts printf style arguments.

## [0.0.8] - 2023-06-19

### Updated

- `def` syntax annotation updated.
- `define` and `typedef` removed.
- Updated `$include` syntax.
- Fix of `.#x` syntax

## [0.0.7] - 2023-05-15

### Added

- Pair quotes.
- Initial run configuration.
- Breadcrumbs for some constructs.

### Fixed

- Incorrect parsing of `def` with generic parameters.

## [0.0.6] - 2023-05-11

### Added

- Allow IDE .c3 file association.
- Smart brace pair.

### Updated

- String parsing stability.
- b64 and hex bytes correctly parsed and checked.

## [0.0.5] - 2023-05-06

### Added

- Brace matching.
- Top level code completion.
- Some breadcrumbs.

### Updated

- Matches latest syntax updates.

## [0.0.4] - 2023-05-05

### Added

- Support `def` keyword.

### Updated

- Types now get colored before semantic analysis.

## [0.0.3] - 2023-05-03

### Added

- Some semantic highlighting.
- Color settings.
- Commenter.

## [0.0.2] - 2023-04-28

### Updated

- Grammar fixes.
- icon updates.

## [0.0.1] - 2023-04-27

- First alpha.



