import {useState} from "react";
import {useJsApiLoader} from "@react-google-maps/api";
import MapComponent from "./MapComponent.jsx";
import SearchBarComponent from "./SearchBarComponent.jsx";
import PlaceTypeSelector from "./select-place-type/PlaceTypeSelector.jsx";

const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const googleMapsLibrary = ["places"]

const GoogleMapComponent = ({onLocationChange, onTypeSelect}) => {
    const [markerPosition, setMarkerPosition] = useState(null);
    const {isLoaded} = useJsApiLoader({
        id: 'google-map-script',
        googleMapsApiKey: GOOGLE_MAPS_API_KEY,
        libraries: googleMapsLibrary,
    });
    const onPlacesChanged = (searchBox) => {
        if (!searchBox) return;
        const places = searchBox.getPlaces();
        if (!places.length) return;
        const targetPlaceLocation = places[0].geometry.location;
        setMarkerPosition(targetPlaceLocation);
        onLocationChange({lat: targetPlaceLocation.lat(), lng: targetPlaceLocation.lng()});
    };

    const onMapClick = (event, map) => {
        if (map) {
            map.panTo(event.latLng);
            setMarkerPosition(event.latLng);
            onLocationChange({lat: event.latLng.lat(), lng: event.latLng.lng()});
        }
    }

    if (!isLoaded) {
        return (<div>Loading...</div>)
    }
    return (
        <div className="flex flex-col gap-4 p-4">
            <div>
                <SearchBarComponent onPlacesChanged={onPlacesChanged}/>
            </div>
            <div>
                <PlaceTypeSelector onSelect={onTypeSelect}/>
            </div>
            <div className="flex-grow flex items-center justify-center">
                <MapComponent markerPosition={markerPosition} onClick={onMapClick}/>
            </div>
        </div>
    );
};

export default GoogleMapComponent;
