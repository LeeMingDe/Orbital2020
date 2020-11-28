# lastMinute

### <h3> Scope of Project

Our application provides an all in one solution which can be used for travelling. The application will consist of 4 main functions. These functions are the trips function, diary function, map function and currency converter function.

**Core Features of the application (essential to have):**

A built-in planner that allows the user to plan for the trips that they would like to go to. Users can add specific activities with details like destination, date, time and description about each activity.

A built-in diary function that allows the user to record down their experiences.

A map function that will make use of Google maps for users to use to navigate to their desired location.

A currency converter that allows users to view the exchange rates and calculate how much money they would have to exchange based on live rates (ratesapi.io).



### <h3>Program Flow
<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/ProgramFlow.PNG" width="700" height="500"/>

**Description**

**Login:**

Once users start up the application, they can choose to register with a username, email and password or login with an existing account. For users who forgot their password, they can choose the ”Forget Password” to reset their password using their email. 

All user information is stored in the Firebase database.

**Main Page:**

After logging in, there will be a bottom navigation menu for the user to navigate between different functions. 

**Trips page**
* Users can build their itinerary by adding new trips and activities.
* Users can share their trips with others.
* Data stored in Firebase Firestore.

**Diary page**
* Users can create new diary entries by keying in a title and their journal content.
* Users can attach photos from either media gallery or take a photo with their camera.
* Users can attach a location.
* Data stored in Firebase Firestore.

**Map page**
* Users can see their location on the map.
* Users can get directions and route to the place that they want to go.
* Users can get ratings of some of the places around them.
* Implemented with Google Maps API.

**Currency Converter Page**
* Users can use a spinner (drop-down option) to select the base currency (33 currencies to select from).
* Users can choose to input his/her own rate as well.
* Implemented with ratesapi.io (data based on the European Central Bank).

**Settings page**
* Users can choose to log out which will direct them back to the login page.
* Users can change their username, email or password.
* Users can set a daily notification reminder to remind them to create a journal entry.
* Users can look at faq on how to use the application.

### <h3> UI Design
  
**Login Page:**
  
<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/loginPage.png" width="167" height="338"/> <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/register.png" width="167" height="338"/> <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/PasswordRecovery.png" width="167" height="338"/>
 
 <pre>   Login Page            Registration          Password Reset </pre>

**Trips Page:**

<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Trips.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/AddTrips.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Activities.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/AddActivities.png" width="167" height="338"/>

 <pre>    Trips Page             Add Trip            Activities Page           Add Activity</pre>
                     
**Diary page:**

<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Diary.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/AddDiary.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/ViewDiary.png" width="167" height="338"/>

 <pre>    Diary Page             Add entries            View entries</pre>

**Map Page:**

<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Location.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/SearchMaps.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Navigate.png" width="167" height="338"/>

 <pre>     Map Page                 Search                 Places</pre>
                        

**Currency Converter Page:**

<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/CurrencyConverter.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/PersonalRates.png" width="167" height="338"/>

 <pre>International rates      Personal rate</pre>

**Settings Page:**

<img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Settings.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/General.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Notifications.png" width="167" height="338"/>  <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Security.png" width="167" height="338"/>

 <pre>   Settings page          General Page         Notification Page         Security Page</pre>
 
 <img src="https://github.com/LeeMingDe/Orbital2020/blob/master/images/Help.png" width="167" height="338"/>
   
 <pre>    Help Page</pre>
                       
### <h3> Installation
  Simply sign up for an Firebase Account and create a project to get an google-service.json file. Paste the google-service.json file
  into App folder to link it to firebase.
  Sign up for Google Maps API and paste the API key into the google maps_api.xml file in "../app/release/res/values".
  
  **Without Android phone:**

1. Download and install Android Studio (Ensure that AVD device is ticked) https://developer.android.com/studio#downloads
2. Open up Android Studio and choose the option “Get From Version Control” and paste in the URL https://github.com/LeeMingDe/Orbital2020.git and click clone
3. Select the folder and click ok.
4. Click on the “No Device” button that is to the left of the run button and select the option “Open up the AVD Manager”
5. Create a virtual device with the phone category and pixel 3 as the device and click next.
6. Download API 29.0 with the name Q. Click next and finish the creation of the device
7. Now, just click on the run button to run our application. (It might take some time for the virtual device to load)

(If unable to run the application, go to File -> Invalidate Caches/Restart and then try to run the application again)

(If still unable to run the application, go to Build -> Rebuild Project)

**With Android phone (API 28 and above):**

1. Download the APK
2. Transfer APK to phone
3. Install our application and run it

### Credits
* Country Flags in Currency Converter by CountryFlags - https://www.countryflags.com/en/
* All currency rates by RateApi - http://ratesapi.io/
* Remember EditText dependency by Mark Zhai - https://github.com/markzhai/RememberEditText 
* Icons in Settings page by Icons8 - https://icons8.com 
* Reference of Animation Resource files in Settings help page by Coding in Flow - https://codinginflow.com/tutorials/android/slide-animation-between-activities 
