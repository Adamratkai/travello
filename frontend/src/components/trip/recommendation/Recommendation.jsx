import {useEffect, useState} from "react";
import RecommendedPlace from "./RecommendedPlace.jsx";
import RecommendationDetailedPlace from "./RecommendationDetailedPlace.jsx";
import useAxios from "../../../hooks/useAxios.js";

function Recommendation({location, onAddPlace, placeType}) {
    const [recommendations, setRecommendations] = useState([]);
    const [selectedPlaceId, setSelectedPlaceId] = useState(null);
    const axiosInstance = useAxios();

    useEffect(() => {
        const abortController = new AbortController();

        async function fetchRecommendations() {
            try {

                const response = await axiosInstance.get(`/api/location/recommendations/`, {
                    params: {
                        location: `${location.lat},${location.lng}`,
                        type: placeType,
                    },
                    signal: abortController.signal,
                });
                setRecommendations(response.data);
            } catch (error) {
                console.error("Error fetching recommendations:", error);
            }
        }

        fetchRecommendations();
        return () => {
            abortController.abort();
        };
    }, [location.lat, location.lng]);

    function handlePlaceClick(placeId) {
        setSelectedPlaceId(placeId);
    }

    function handleCloseDetailedPlace() {
        setSelectedPlaceId(null);
    }

    return (
        <>
            {recommendations && (
                <div className="recommendation-container overflow-x-auto px-4 box-border">
                    <div className="flex gap-5">
                        {recommendations.map((recommendation) => (
                            <RecommendedPlace
                                key={recommendation.place_id}
                                name={recommendation.name}
                                price={recommendation.price_level}
                                rating={recommendation.rating}
                                onPlaceClick={() => handlePlaceClick(recommendation.place_id)}
                            />
                        ))}
                    </div>
                </div>
            )}
            {selectedPlaceId && (
                <RecommendationDetailedPlace
                    placeId={selectedPlaceId}
                    onPlaceClose={handleCloseDetailedPlace}
                    onAddPlace={onAddPlace}
                />
            )}
        </>
    );
}

export default Recommendation;
