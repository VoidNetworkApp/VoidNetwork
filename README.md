# VoidNetwork

Void Network is an immersive mobile application inspired by the **Stranger Things** universe. It enables cross-dimensional communication through location-based portals and binary-coded signals, blending real-world sensor interactions with a fictional alternate dimension.

### Mobile Computing (2024/2025) - Group 8
- 56337 Diogo Pedro
- 56274 Manuel Cardoso
- 64371 Ricardo Costa

![](https://skillicons.dev/icons?i=kotlin,androidstudio,firebase,supabase)

---

## Concept

Void Network allows users to interact with others in the Upside Down dimension, with the following key features:

- **Binary-Coded Communication**
	- Users can communicate through custom binary-coded languages for signal translation
	- Messages can be recorded using taps or luminosity signals, as well as automatically using the language dictionary
	- Messages are received through vibration and flashlight signals and push notifications, even when the app is closed
- **Location-based Portals**
	- Users can register portals to enable communication between both dimensions if within portal range
	- Using image detection, the app validates the presence of a portal (e.g., a tree) through a photo taken with the camera
	- Portals are displayed in a map along with the users location
- **Upside Down State**
	- Triggered by low light levels, detected with the luminosity sensor
	- Transitions between light and dark themes to indicate whether the user is in the real world or the Upside Down
	- Changes app functionalities and interactions based on the current state

## Technologies

- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Model-View-ViewModel (MVVM)
- **Backend Services**:
  - **Firebase Auth**: For user authentication
  - **Firebase Real-time Database**: For real-time message and portal synchronization
  - **Supabase**: For file storage (e.g., portal photos)
- **APIs**:
  - **Mapbox**: For map visualization and street name fetching
  - **ML Kit**: For portal (tree) image detection

## Sensors

- **GPS**: Tracks user location and checks portal proximity
- **Luminosity Sensor**: Detects light levels to change between real-world and Upside Down states and also for building light signals
- **Camera**: Captures portal images for portal registration

## Device Features

- **Vibration**: Sends binary signals as vibration patterns
- **Flashlight**: Transmits signals as light patterns
- **Push Notifications**: Notifies users of received signals, using a foreground service


## Documentation

The project documentation can be found in the [`docs`](./docs) folder.

[Promotional Video](./docs//video.mp4)