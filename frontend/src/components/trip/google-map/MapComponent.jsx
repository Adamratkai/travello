import {GoogleMap, Marker} from '@react-google-maps/api';
import {useState} from 'react'
import {useMarkers} from "../../MarkersContext.jsx";

const containerStyle = {
    width: "400px",
    height: "400px",
};
const defaultCenter = {lat: -33.8688, lng: 151.2195};

const customIcon = {
    url: "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"
};

const selectedIcon = {
    url: "https://maps.google.com/mapfiles/ms/icons/green-dot.png"
}


const MapComponent = ({markerPosition, onClick}) => {
    const [map, setMap] = useState(null);
    const {markers} = useMarkers()


    return (
        <GoogleMap
            mapContainerStyle={containerStyle}
            center={markerPosition || defaultCenter}
            zoom={15}
            onLoad={setMap}
            onClick={(event) => onClick(event, map)}
        >
            {markerPosition && <Marker position={markerPosition}/>}
            {markers && markers.map((markerPos, index) => (
                <Marker key={index} position={{lat: markerPos.lat, lng: markerPos.lng}}
                        icon={markerPos.selected ? selectedIcon : customIcon}/>))}
        </GoogleMap>
    );
};

export default MapComponent