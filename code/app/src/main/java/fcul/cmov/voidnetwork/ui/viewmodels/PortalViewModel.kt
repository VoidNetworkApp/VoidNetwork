package fcul.cmov.voidnetwork.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import fcul.cmov.voidnetwork.domain.Portal

class PortalViewModel : ViewModel() {
    // handles portal selection
    // handles portal registration with camera and vision api
    // gets the portal locations (coordinates and street names)

    val portalSelected by mutableStateOf<Portal?>(null)
}