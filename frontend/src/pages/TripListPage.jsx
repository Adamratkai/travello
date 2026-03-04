import {useEffect, useState} from "react";
import AddTrip from "../components/trip/add-trip/AddTrip.jsx";
import useAxios from "../hooks/useAxios.js";
import {useNavigate} from "react-router-dom";

export default function TripListPage() {

    const [trips, setTrips] = useState([]);
    const navigate = useNavigate();

    const axiosInstance = useAxios();
    useEffect(() => {
        const abortController = new AbortController();

        async function fetchTrips() {
            try {
                const response = await axiosInstance.get("/api/trips/traveller", {
                    signal: abortController.signal,
                });
                setTrips(response.data);
            } catch (error) {
                console.error("Error fetching trips:", error);
            }
        }

        fetchTrips();
        return () => {
            abortController.abort();
        };
    }, []);


    function handleClick(tripId) {
        navigate(`/trip-editor/${tripId}`);
    }

    function handleTripAdd(newTrip) {
        setTrips((prevTrips) => [...prevTrips, newTrip]);
    }

    return (
        <div className="card flex flex-col bg-base-200  items-center  min-h-2/3 min-w-1/2 p-5 ">
            <h2 className="justify-start">Trips</h2>
            {trips.length > 0 && trips.map((trip) => (
                <div key={trip.tripId}>
                    <button className="link link-hover" onClick={() => handleClick(trip.tripId)}>
                        <strong>{trip.name}</strong></button>
                    <div>Start: {trip.startDate}</div>
                    <div>End: {trip.endDate}</div>
                </div>

            ))}

            <AddTrip className="justify-end" onTripAdd={handleTripAdd}/>
        </div>
    );
}
