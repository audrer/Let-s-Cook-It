# Let's Cook It Android Application

Welcome to the Let's Cook It Recipe Android Application repository! This application is designed to help users discover, save, and customize their favorite recipes. Built entirely using Kotlin and Jetpack Compose, it incorporates modern Android development practices, asynchronous programming, local database caching, and a custom API for a seamless user experience. 

## Video Preview

https://github.com/JatinVashisht1/Let-s-Cook-It/assets/81457315/b158e1ee-0694-433b-9bb9-3bb1022bc78f

## Features


- **100% Jetpack Compose UI:** The application boasts a beautiful and responsive user interface created entirely with Jetpack Compose, ensuring a smooth user experience.

- **Kotlin Coroutines and Flows:** Asynchronous tasks are handled efficiently using Kotlin Coroutines and Flows, ensuring a responsive application while managing background tasks seamlessly.

- **Local Database Caching:** Data displayed to users is first saved to a local Room database, providing offline access and faster loading times.

- **Custom Pagination Logic:** Implemented custom pagination logic to optimize performance by loading data incrementally and preventing overwhelming the user with excessive content.

- **Save Favorite Recipes:** Users can save their favorite recipes to the local database for quick access, even when offline.

- **Dynamic Ingredient Adjustments:** Users can dynamically adjust the amount of ingredients according to the number of people, making it easy to scale recipes for different serving sizes.

- **Offline Access:** Once users load any category or recipe, it is stored in the local database. Subsequent access does not require an internet connection, enhancing the app's usability.

- **Clean Architecture and SOLID Principles:** The project is structured using clean architecture principles, ensuring separation of concerns and adherence to SOLID principles for maintainability and scalability.

- **MVVM/MVI Architecture:** The UI layer follows the MVVM/MVI architecture, promoting a clear separation of concerns and enhancing testability.

## API Details

The application relies on a custom API created using web scraping techniques. The API is hosted on GitHub Pages as static files, providing reliable data for the app's functionality.
The API can be accessed at [https://recipeapi.github.io](https://recipeapi.github.io)

## How to Run

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and run the application on an emulator or a physical device with Android OS version 5.1 and above.

## Getting Started

To get started with the Recipe Android Application, follow these steps:

1. **Prerequisites:** Ensure you have Android Studio installed on your development machine.

2. **Clone the Repository:** Use the following command to clone the repository to your local machine:
