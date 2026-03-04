import GoogleMapComponent from '../components/trip/google-map/GoogleMapComponent.jsx'
import Recommendation from "../components/trip/recommendation/Recommendation.jsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import useAxios from "../hooks/useAxios.js";
import TripDetails from "../components/trip/trip-details/TripDetails.jsx";
import {useMarkers} from "../components/MarkersContext.jsx";

function TripPage() {
    const [location, setLocation] = useState(null);
    const [tripDetail, setTripDetail] = useState(null);
    const [activities, setActivities] = useState(null);
    const [placeType, setPlaceType] = useState(null);
    const {tripId} = useParams();
    const axiosInstance = useAxios();
    const {setMarkers} = useMarkers();

    function handleLocationChange(location) {
        setLocation(location);
    }

    async function handleAddPlace(place) {
        try {
            const response = await axiosInstance.post(`/api/trip-activities/${tripId}`, {
                placeId: place.placeId,
                visitTime: tripDetail.startDate + "T00:00:00",
            });
            setActivities((prev) => [...prev, {placeDTO: place, visitTime: (tripDetail.startDate + "T00:00:00")}]);
            return response.data;
        } catch (error) {
            console.error("Error fetching places:", error);
        }
    }

    function handleSelectPlaceType(type) {
        setPlaceType(type);
    }

    useEffect(() => {
        const abortController = new AbortController();

        async function fetchPlaces() {
            try {

                const response = await axiosInstance.get(`/api/trips/${tripId}`, {
                    signal: abortController.signal,
                });
                setTripDetail(response.data);
                setActivities(response.data.tripActivities);
                response.data.tripActivities.map(activity => {
                    setMarkers(prev => [...prev, {
                        lat: activity.placeDTO.location.lat,
                        lng: activity.placeDTO.location.lng,
                        selected: false
                    }]);
                })
            } catch (error) {
                console.error("Error fetching places:", error);
            }
        }

        fetchPlaces();
        return () => {
            abortController.abort();
        };
    }, []);

    return (
        <div className="flex flex-col p-4">
            <div className="flex flex-grow justify-center gap-4 mb-4">
                <GoogleMapComponent onLocationChange={handleLocationChange} onTypeSelect={handleSelectPlaceType}/>
                {activities &&
                    <TripDetails tripDetail={tripDetail} activities={activities}/>
                }</div>
            {location &&
                <div className="flex justify-center mt-4 max-w-[80vw] overflow-y-auto px-4 box-border">
                    <Recommendation location={location} onAddPlace={handleAddPlace} placeType={placeType}/>
                </div>
            }
        </div>

    )
}

export default TripPage
