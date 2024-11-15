# VoidNetwork

App that enables immersive, cross-dimensional communication for the Stranger Things universe, featuring voice messaging through portals, binary-coded signals and an '80s music player

### Mobile Computing (2024/2025) - Group 8
- 56337 Diogo Pedro
- 56274 Manuel Cardoso
- 64371 Ricardo Costa

---

## Motivation

*Stranger Things* is a unique universe set in the 80s with a distinct division between the real world and the Upside Down. 

The Upside Down is a mysterious alternate dimension existing in parallel to the human world. This app allows characters in this fictional world to communicate across both dimensions.

- **Goal**: Enable communication with alternate realities, blending location-based features with immersive communication.
- **Related Apps**:
  - **Pokemon GO**: Blends real and fictional worlds through GPS and the camera.
  - **Discord/WhatsApp**: Real-time communication with others.


## Concept and Background

- **Location-based Portals**: Users can register existing portals to enable voice communication if they are close enough.
  - Using an image detection API, the app validates the presence of a portal (e.g., a tree) through the camera.
  - If a user is within a specified distance of a portal, they can send a voice message that all users near other portals can receive in real-time.


## Main Envisioned Features

- **Vibration/Light-based Communication**: When far from a portal, users can communicate using taps (emitting vibration) or by using the luminosity sensor to send flashlight signals.
  - To simplify communication, we will develop a custom binary language API for creation and translation. This allows users to have a unique language that can be automatically translated. By default, Morse code will be included as an option.


## 80’s Music Player

Users can listen to 1980s music featured in the TV show, without needing a cassette player or Walkman.

- Play '80s classics
- Listen along with friends
- Custom playlists


## Technologies

- **Device Sensors**:
  - **GPS** for registering portal locations.
  - **Luminosity sensor** for light-based communication.
- **Vibration**: Enables touch-based communication over distance.
- **Google Cloud Vision API**: Detects portals through camera photos.
- **Firebase**: Centralized service for multi-user functionality and real-time communication.
  - **Firestore**: Stores language translation mappings and portal GPS coordinates.
  - **Cloud Functions**: Performs binary language translations in real-time and utilizes the image detection API.


