# VoidNetwork

App that enables immersive, cross-dimensional communication for the Stranger Things universe, with binary-coded signal messages through Upside Down portals

### Mobile Computing (2024/2025) - Group 8
- 56337 Diogo Pedro
- 56274 Manuel Cardoso
- 64371 Ricardo Costa

![](https://skillicons.dev/icons?i=kotlin,androidstudio,firebase)

---

## Motivation

*Stranger Things* is a unique universe set in the 80s with a distinct division between the real world and the Upside Down. 

The Upside Down is a mysterious alternate dimension existing in parallel to the human world. This app allows characters in this fictional world to communicate across both dimensions.

- **Goal**: Enable communication with alternate realities, blending location-based features with immersive communication.
- **Related Apps**:
  - **Pokemon GO**: Blends real and fictional worlds through GPS and the camera.
  - **Discord/WhatsApp**: Real-time communication with others.


## Concept

- **Vibration/Light-based Communication**
	- Users can communicate through custom binary-coded languages:
	- Custom binary languages for signal translation.
	- Messages can be recorded using taps or luminosity signals, as well as automatic messages using the language dictionary
	- Messages are received through vibration and flashlight signals and push notifications.
- **Location-based Portals**
	- Users can register portals to enable communication between both dimensions if within portal range.
	- Using an image detection API, the app validates the presence of a portal (e.g., a tree) through the camera.
	- If a user is within range of a portal, they can send and receive messages.
	- Portals are displayed in a map along with the users location.
- **Upside Down State**
	- If a user is in the dark long enough, it is considered to be in the Upside Down
	- Using the luminosity sensor, changes Upside Down state and the theme (real world uses light theme and Upside Down uses dark theme).
	- This changes how the app works and the functionalities available to the user.

## Technologies
- **Language & Frameworks:**
	- Kotlin, Android, Jetpack Compose.
- **Device Sensors**:
	- **GPS** for registering portal locations.
	- **Luminosity sensor** for light-based communication.
	- **Camera** for capturing image required to register a portal.
- **Other Device Features**:
	- **Vibration** and flashlight **for** signal communication.
	- **Foreground service** to deliver push notifications.
- **Mapbox:** To display the portals and the user's location the map.
- **ML Kit**: To detect portals through images captured with the camera.
- **Firebase**: Centralized service for multi-user functionality and real-time communication.
	- **Real-Time Database**: Stores language translation mappings and portal GPS coordinates.
	- **Firebase Auth:** To distinguish which users sent which messages.


