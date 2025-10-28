# Salesman Application

A simple Android app built in Kotlin using Jetpack Compose and MVI architecture.

## Tech stack
- Kotlin
- Jetpack Compose
- Hilt (DI)
- Coroutines / Flow
- MVI architecture
- Unit tests (JUnit, MockK)

## Features
- List of salesmen with name and working areas
- Search field with 1s debounce delay
- Filtering by postcode expressions (e.g. `762*`)

## Project structure
- `core` – dependency injection setup
- `data` – repository implementation and models
- `domain` – business logic (use cases)
- `ui` – composables, viewmodels and screens

## Notes
Due to time constraints, UI may differ slightly from the Adobe design.