import {useEffect, useRef, useState} from "react";
import useAxios from "../../../hooks/useAxios.js";
import PhotosSlideShow from "../../photo/PhotosSlideShow";
import {useMarkers} from "../../MarkersContext.jsx";

const MAX_RATING = 5;

function RecommendationDetailedPlace({placeId, onPlaceClose, onAddPlace}) {
    const [place, setPlace] = useState(null);
    const modalRef = useRef(null);
    const axiosInstance = useAxios();
    const {setMarkers} = useMarkers()
    useEffect(() => {
        const abortController = new AbortController();

        async function fetchPlace() {
            try {
                const response = await axiosInstance.get(`/api/location/${placeId}`, {
                    signal: abortController.signal,
                });
                setPlace(response.data);
            } catch (error) {
                console.error("Error fetching detailed place:", error);
            }
        }

        fetchPlace();
        return () => {
            abortController.abort();
        }
    }, []);

    function handleAddPlace() {
        onPlaceClose();
        onAddPlace(place);
        setMarkers(prev => [...prev, place.location]);
    }

    function closeModal(e) {
        if (e.target === modalRef.current) {
            onPlaceClose();
        }
    }

    return (
        <>
            {place && (
                <dialog ref={modalRef} id="my_modal_2" className="modal modal-open" onClick={closeModal}>
                    <div className="card bg-base-100 w-96 shadow-sm">
                        <figure className="max-h-52">
                            <PhotosSlideShow photos={place.photos}></PhotosSlideShow>
                        </figure>
                        <div className="card-body">
                            <p className="card-title justify-center">{place.name}</p>
                            <p>Price: {"$".repeat(Math.max(1, Math.floor(place.priceLevel)))}</p>
                            <div className="rating justify-center">
                                {[...Array(MAX_RATING)].map((_, index) => (
                                    <div
                                        key={index}
                                        className="mask mask-star"
                                        aria-label={`${index + 1} star`}
                                        aria-current={index + 1 === Math.round(place.rating) ? "true" : undefined}
                                    ></div>
                                ))}
                                {place.rating}
                            </div>
                            <p>Opening hours:</p>
                            {place.openingHours ? (
                                <div>
                                    {place.openingHours.map((item, index) => (<p key={index}>{item}</p>))}
                                </div>
                            ) : (<p>Not available</p>)}
                            <div className="card-actions justify-end">
                                <button className="btn btn-primary" onClick={handleAddPlace}>Add place</button>
                            </div>
                        </div>
                    </div>
                </dialog>
            )}
        </>
    );
}

export default RecommendationDetailedPlace;
