<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# C3IntelliJ Changelog

## [Unreleased]
- function and import completion

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



