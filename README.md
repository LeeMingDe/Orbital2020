# lastMinute

# <h3> Scope of Project

Our application provides an all in one solution which can be used for travelling. The application will consist of 4 main functions. These functions are the trips function, diary function, map function and currency converter function.

**Core Features of the application (essential to have):**

A built-in planner that allows the user to plan for the trips that they would like to go to. Users can add specific activities with details like destination, date, time and description about each activity.

A built-in diary function that allows the user to record down their experiences.

A map function that will make use of Google maps for users to use to navigate to their desired location.

A currency converter that allows users to view the exchange rates and calculate how much money they would have to exchange based on live rates (ratesapi.io).



# <h3>Program Flow

![Flow diagram]()

Description

Login:

Once users start up the application, they can choose to register with a username, email and password or login with an existing account. For users who forgot their password, they can choose the ”Forget Password” to reset their password using their email. 

All user information is stored in the Firebase database.

Main Page:

After logging in, there will be a bottom navigation menu for the user to navigate between different functions. 

Trips page
Users can build their itinerary by adding new trips and activities.
Users can share their trips with others.
Data stored in Firebase Firestore.
Diary page
Users can create new diary entries by keying in a title and their journal content.
Users can attach photos from either media gallery or take a photo with their camera.
Users can attach a location.
Data stored in Firebase Firestore.

Map page
Users can see their location on the map.
Users can get directions and route to the place that they want to go.
Users can get ratings of some of the places around them.
Implemented with Google Maps API.

Currency Converter Page
Users can use a spinner (drop-down option) to select the base currency (33 currencies to select from).
Users can choose to input his/her own rate as well.
Implemented with ratesapi.io (data based on the European Central Bank).

Settings page
Users can choose to log out which will direct them back to the login page.
Users can change their username, email or password.
Users can set a daily notification reminder to remind them to create a journal entry.
Users can look at faq on how to use the application.
