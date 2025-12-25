# MyntWeather

A modern Android weather application built with Jetpack Compose that provides real-time weather information based on your location, along with weather history tracking and secure user authentication.

## Project Description

MyntWeather is a feature-rich weather application that allows users to:
- **Register and Login** with secure authentication
- **View Current Weather** based on their GPS location
- **Track Weather History** to see past weather conditions
- **Store Data Securely** using encrypted SQLCipher database
- **Offline Support** with local data caching

The app follows Clean Architecture principles with MVVM pattern, ensuring maintainability, testability, and scalability.

## Features

### Authentication
- User registration with validation
- Secure login functionality
- Password hashing for security
- Form validation with real-time error feedback

### Weather Information
- Real-time weather data from OpenWeather API
- Location-based weather updates using GPS
- Current weather display with:
  - Temperature in Celsius
  - Weather condition and icon
  - Sunrise and sunset times
  - City and country information
- Weather history tracking per user
- Refresh functionality for latest weather data

### Data Management
- Encrypted local database using SQLCipher
- Room database for efficient data storage
- Secure user preferences management
- Offline data access

### User Interface
- Modern Material Design 3 UI
- Jetpack Compose for declarative UI
- Smooth navigation with bottom navigation bar
- Responsive and intuitive user experience

## Architecture

The project follows **Clean Architecture** principles with clear separation of concerns:

```
app/
├── data/           # Data layer (Repository implementations, API, Database)
├── domain/         # Domain layer (Use cases, Repository interfaces, Models)
├── ui/             # Presentation layer (Compose UI, ViewModels)
├── di/             # Dependency Injection modules
└── utils/          # Utility classes and helpers
```

### Architecture Components:
- **MVVM Pattern**: ViewModels manage UI state and business logic
- **Repository Pattern**: Abstracts data sources (Remote API, Local Database)
- **Use Cases**: Encapsulate business logic
- **Dependency Injection**: Hilt for managing dependencies

## Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - UI components and theming

### Architecture & Dependency Injection
- **Hilt** - Dependency injection framework
- **KSP** - Kotlin Symbol Processing

### Networking
- **Retrofit** - HTTP client for API calls
- **OkHttp** - HTTP client with logging interceptor
- **Gson** - JSON serialization/deserialization

### Local Storage
- **Room** - SQLite database abstraction
- **SQLCipher** - Encrypted database
- **DataStore/SharedPreferences** - User preferences storage
- **Android Security Crypto** - Secure key storage

### Location Services
- **Google Play Services Location** - GPS location tracking

### Image Loading
- **Coil Compose** - Image loading library

### Testing
- **JUnit** - Unit testing framework
- **Mockito** - Mocking framework
- **Turbine** - Flow testing
- **Robolectric** - Android unit testing
- **Kotlin Coroutines Test** - Coroutine testing utilities

## Prerequisites

Before you begin, ensure you have the following installed:
- **Android Studio** (latest version recommended)
- **JDK 11** or higher
- **Android SDK** (API 24 minimum, API 36 target)
- **OpenWeather API Key** - Get your free API key from [OpenWeatherMap](https://openweathermap.org/api)
- **SQLCipher Master Key** - For database encryption

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <https://github.com/CPR-MYNT/MyntWeather.git>
cd MyntWeather
```

### 2. Configure API Keys

Create a `local.properties` file in the root directory (if it doesn't exist) and add your API keys:

```properties
OPEN_WEATHER_API_KEY=your_openweather_api_key_here
MASTER_KEY_SQL_CIPHER=your_sqlcipher_master_key_here
```

### 3. Build the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project (Build → Make Project)
4. Run the app on an emulator or physical device

### 4. Grant Permissions

The app requires the following permissions:
- **Internet** - For fetching weather data
- **Location (Fine/Coarse)** - For GPS-based weather updates

Make sure to grant location permissions when prompted.

## Testing

The project includes comprehensive unit tests covering:
- Repository implementations
- Use cases
- ViewModels
- Utility classes
- Data validation

### Test Coverage Screenshots
<img width="795" height="956" alt="Screenshot 2025-12-02 124724" src="https://github.com/user-attachments/assets/a10b3d3b-fb5b-4c69-a32c-cd0edc310c79" />


### App Screenshots
<img width="324" height="620" alt="Screenshot_20251202_143136" src="https://github.com/user-attachments/assets/4a0cdf3b-0dad-4162-b787-b787d1acb59f" />

<img width="324" height="620" alt="Screenshot_20251202_131315" src="https://github.com/user-attachments/assets/c77f08d3-c77a-47eb-8963-59c6f4aeed9a" />

<img width="324" height="620" alt="Screenshot_20251202_131445" src="https://github.com/user-attachments/assets/572c77d7-5c9b-45c8-8f64-9e5ab66ef2f0" />

<img width="324" height="620" alt="Screenshot_20251202_131513" src="https://github.com/user-attachments/assets/6b5b8ec0-61c2-47b5-9fee-3950fe58564e" />

<img width="324" height="620" alt="Screenshot_20251202_131529" src="https://github.com/user-attachments/assets/826da16b-ff3a-4731-b95e-e1829d42d324" />


## Project Structure

```
MyntWeather/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/perennial/weather/
│   │   │   │   ├── app/              # Application class
│   │   │   │   ├── data/              # Data layer
│   │   │   │   │   ├── local/         # Local database (Room, SQLCipher)
│   │   │   │   │   ├── remote/        # Remote API (Retrofit)
│   │   │   │   │   └── repository/    # Repository implementations
│   │   │   │   ├── domain/            # Domain layer
│   │   │   │   │   ├── model/         # Domain models
│   │   │   │   │   ├── repository/    # Repository interfaces
│   │   │   │   │   └── usecase/       # Use cases
│   │   │   │   ├── di/                # Dependency injection modules
│   │   │   │   ├── ui/                # UI layer
│   │   │   │   │   ├── auth/          # Authentication screens
│   │   │   │   │   ├── home/          # Home screens
│   │   │   │   │   ├── navigation/    # Navigation setup
│   │   │   │   │   ├── splash/        # Splash screen
│   │   │   │   │   ├── components/    # Reusable components
│   │   │   │   │   └── theme/         # App theming
│   │   │   │   └── utils/             # Utility classes
│   │   │   └── res/                   # Resources
│   │   └── test/                      # Unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml             # Dependency version catalog
├── build.gradle.kts
└── settings.gradle.kts
```

## Security Features

- **Password Hashing**: Passwords are hashed before storage
- **Encrypted Database**: SQLCipher encryption for sensitive data
- **Secure Key Storage**: Android Security Crypto for API keys
- **Input Validation**: Comprehensive form validation

## API Integration

The app integrates with **OpenWeather API** to fetch:
- Current weather conditions
- Temperature data
- Weather icons
- Sunrise/sunset times
- Location information

---

**Note**: Being only contributor I have put all code in master branch.

