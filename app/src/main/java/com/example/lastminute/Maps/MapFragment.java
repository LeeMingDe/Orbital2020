package com.example.lastminute.Maps;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lastminute.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener
    , GoogleMap.OnPolylineClickListener {
    private GoogleMap gMap;
    private EditText inputSearch;
    private FloatingActionButton locateUser, placePick;
    private FusedLocationProviderClient locationProviderClient;
    private boolean locationPermission;
    private Location mLocation;
    private PlacesClient placesClient;
    private GeoApiContext geoApiContext = null;
    private ArrayList<PolyLinesData> mPolyLinesData = new ArrayList<>();

    /*
    Fields for bottom sheet window
     */
    private BottomSheetBehavior bottomSheetBehavior, placePickerBehavior;
    private LinearLayoutCompat bottomSheet, placePicker;
    private TextView placeName, ratings, addressDetails;
    private ImageView placesPhoto;
    private Button routes;
    private RatingBar ratingBar;

    /*
    Fields for listview
     */
    private ListView listView;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private double[] mLikelyPlaceRatings;
    private Bitmap[] mLikelyPlacePhoto;
    private static final int M_MAX_ENTRIES = 1;

    private final static float DEFAULT_ZOOM = 15f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST = 0;
    private static final int SEARCH_REQUEST = 1;
    private static final String TAG = "MapFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_map, container, false);
        MapsInitializer.initialize(getActivity());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initializePlacesSDK();
                getLocationPermission();
            }
        });
        thread.start();
        setUpUIView(v);
        initializeMap();
        return v;
    }

    private void setUpUIView(View v) {
        inputSearch = (EditText) v.findViewById(R.id.inputSearch);
        locateUser = (FloatingActionButton) v.findViewById(R.id.locateUser);
        bottomSheet = v.findViewById(R.id.bottom_sheet);
        placeName = (TextView) v.findViewById(R.id.placeName);
        ratings = (TextView) v.findViewById(R.id.ratings);
        addressDetails = (TextView) v.findViewById(R.id.addressDetails);
        placesPhoto = (ImageView) v.findViewById(R.id.placesPhoto);
        routes = (Button) v.findViewById(R.id.routes);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        placePicker = v.findViewById(R.id.placePicker);
        placePick = (FloatingActionButton) v.findViewById(R.id.placePick);
        listView = (ListView) v.findViewById(R.id.listPlaces);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        placePickerBehavior = BottomSheetBehavior.from(placePicker);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        placePicker.setVisibility(View.GONE);
    }

    /*
    -----------------------------------------------Google Maps------------------------------------------------
     */

    private void locateMe() {
        locateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    private void initializePlacesSDK() {
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyAeua6lsCCjJVfVn9dRmuhCdQx4qwCizrk");
        placesClient = Places.createClient(getActivity());
        inputSearch.setFocusable(false);
        inputSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG
                , Place.Field.NAME/*, Place.Field.RATING, Place.Field.PHOTO_METADATAS*/);
                LatLng latLngCurrent = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .setInitialQuery(inputSearch.getText().toString())
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setLocationBias(RectangularBounds.newInstance(latLngCurrent, latLngCurrent))
                        .build(getActivity());
                startActivityForResult(intent, SEARCH_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_REQUEST && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            setBottomSheet(place);
            inputSearch.setText(place.getAddress());
            moveCam(DEFAULT_ZOOM, place);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.d(TAG, status.getStatusMessage());
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void getDeviceLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            if (locationPermission) {
                Task location = locationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: " + mLocation);
                            moveCam(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                        } else {
                            Toast.makeText(getActivity(), "Unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "getDeviceLocation error: " + e.getMessage());
        }
    }

    private void getPlacePickerLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            Task location = locationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: " + mLocation);
                        } else {
                            Toast.makeText(getActivity(), "Unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            getCurrentPlaceLikelihoods();
        } catch (Exception e) {
            Log.e(TAG, "getDeviceLocation error: " + e.getMessage());
        }
    }

    private void moveCam(float zoom, Place place) {
        gMap.clear();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoom));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(place.getLatLng());
        gMap.addMarker(markerOptions);
    }

    private void moveCam(float zoom, PointOfInterest poi) {
        gMap.clear();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions()
                    .position(poi.latLng);
        gMap.addMarker(markerOptions);
    }

    private void moveCam(LatLng latLng, float zoom) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext()
                , FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermission = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), permission, LOCATION_PERMISSION_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permission, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermission = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    locationPermission = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gMap = googleMap;
        gMap.setPadding(0, 0 ,0, 40);
        if (locationPermission) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getDeviceLocation();
                    locateMe();
                    placePicker();
                }
            });
            thread.start();
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
            gMap.setOnPoiClickListener(this);
            gMap.setOnPolylineClickListener(this);
        }
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        moveCam(DEFAULT_ZOOM, pointOfInterest);
        String placeID = pointOfInterest.placeId;
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG
                , Place.Field.NAME/*, Place.Field.RATING, Place.Field.PHOTO_METADATAS*/);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeID, fieldList);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                setBottomSheet(place);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
        Log.d(TAG, "onPoiClick: Name " + pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId +
                "\nLatitude:" + pointOfInterest.latLng.latitude +
                " Longitude:" + pointOfInterest.latLng.longitude);
    }

    private void addPolyLinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPolyLinesData.size() > 0){
                    for (PolyLinesData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = gMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.GRAY);
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolyLinesData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                    }
                }
            }
        });
    }

    private void calculateDirections(Place place){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                place.getLatLng().latitude,
                place.getLatLng().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mLocation.getLatitude(),
                        mLocation.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolyLinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for(PolyLinesData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(Color.parseColor("#00BFFF"));
                polylineData.getPolyline().setZIndex(1);
                Marker marker = gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                        .title("Route #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));
                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(Color.GRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


    /*
    ------------------------------------------------Bottom Sheet-----------------------------------------------------
     */

    private void setBottomSheet(Place place) {
        double ratingNum = 0;
        placePicker.setVisibility(View.GONE);
        try {
            ratingNum = place.getRating();
        } catch(NullPointerException e) {
            ratingNum = 0;
        }
        placeName.setText(place.getName());
        addressDetails.setText(place.getAddress());
        ratings.setText((new DecimalFormat("0.0").format(ratingNum)));
        ratingBar.setRating((float) ratingNum);
        retrieveGoogleImage(place);
        calculateDirections(place);
        navigate(place);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void retrieveGoogleImage(Place place) {
        // Get the photo metadata.
        if (place.getPhotoMetadatas() != null) {
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(800) // Optional.
                    .setMaxHeight(400) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    placesPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void retrieveGoogleImage(Place place, final int position) {
        // Get the photo metadata.
        if (place.getPhotoMetadatas() != null) {
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(800) // Optional.
                    .setMaxHeight(400) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    mLikelyPlacePhoto[position] = bitmap;
                    Log.d(TAG, "onSuccess: " + mLikelyPlacePhoto[0]);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void navigate(final Place place) {
        if (locationPermission) {
            routes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri intentUri = Uri.parse("google.navigation:q=" + place.getLatLng().latitude
                            + "," + place.getLatLng().longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Google Maps is not installed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Location Permission not granted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*
    -----------------------------------------------Place picker---------------------------------------------
     */


    private void getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG/*, Place.Field.RATING, Place.Field.PHOTO_METADATAS*/);

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
         final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];
                            mLikelyPlaceRatings = new double[count];
                            mLikelyPlacePhoto = new Bitmap[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Place currPlace = placeLikelihood.getPlace();
//                                try {
//                                    mLikelyPlaceRatings[i] = currPlace.getRating();
//                                } catch (NullPointerException e) {
                                    mLikelyPlaceRatings[i] = 0;
//                                }
//                                retrieveGoogleImage(currPlace, i);
                                mLikelyPlaceNames[i] = currPlace.getName();
                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
                                        null : TextUtils.join(" ", currPlace.getAttributions());
                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();

                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
                                        "" : mLikelyPlaceLatLngs[i].toString();

                                Log.d(TAG, String.format("Place " + currPlace.getName()
                                        + " has likelihood: " + placeLikelihood.getLikelihood()
                                        + " at " + currLatLng));

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }
                            // Populate the ListView
                            fillPlacesList();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }
                });
    }

    private void placePicker() {
        if (locationPermission) {
            placePick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    getPlacePickerLocation();
                    placePicker.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Location Permission not granted",
                    Toast.LENGTH_SHORT).show();
            getLocationPermission();
        }
    }

    private AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // position will give us the index of which place was selected in the array
            LatLng markerLatLng = mLikelyPlaceLatLngs[position];
            String markerSnippet = mLikelyPlaceAddresses[position];
            if (mLikelyPlaceAttributions[position] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[position];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            gMap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            gMap.animateCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        }
    };

    private void fillPlacesList() {
        PlacePickerAdapter placesAdapter = new PlacePickerAdapter();
        listView.setAdapter(placesAdapter);
        listView.setOnItemClickListener(listClickedHandler);
    }

    /*
    ----------------------------------------Place Picker Custom Adapter-----------------------------------
     */

    class PlacePickerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLikelyPlaceNames.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.place_picker_adapter, null);
                ImageView placePickPhoto = (ImageView) convertView.findViewById(R.id.placePickPhoto);
                TextView placeTitle = (TextView) convertView.findViewById(R.id.placeTitle);
                TextView rate = (TextView) convertView.findViewById(R.id.rate);
                TextView placePickerAddress = (TextView) convertView.findViewById(R.id.placePickerAddress);
                RatingBar rb = convertView.findViewById(R.id.rb);

                placePickPhoto.setImageBitmap(mLikelyPlacePhoto[position]);
                placeTitle.setText(mLikelyPlaceNames[position]);
                rate.setText(String.valueOf(mLikelyPlaceRatings[position]));
                placePickerAddress.setText(mLikelyPlaceAddresses[position]);
                rb.setRating((float) mLikelyPlaceRatings[position]);
            }
            return convertView;
        }
    }

}
