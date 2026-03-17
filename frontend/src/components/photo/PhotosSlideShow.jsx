import { useState, useEffect } from "react";
import useAxios from "../../hooks/useAxios.js";

function PhotosSlideShow({ photoIds }) {
    const [currentSlide, setCurrentSlide] = useState(0);
    const [photos, setPhotos] = useState([]);
    const axiosInstance = useAxios();

    useEffect(() => {
        let urls = [];

        async function fetchPhotos() {
            const results = await Promise.all(
                photoIds.map(async (id) => {
                    const res = await axiosInstance.get(`/api/location/photos/${id}`, {
                        responseType: "blob",
                    });

                    const url = URL.createObjectURL(res.data);
                    urls.push(url);
                    return url;
                })
            );

            setPhotos(results);
        }

        fetchPhotos();

        return () => {
            urls.forEach((url) => URL.revokeObjectURL(url));
        };
    }, [photoIds, axiosInstance]);

    const prevSlide = () => {
        setCurrentSlide((current) =>
            current === 0 ? photos.length - 1 : current - 1
        );
    };

    const nextSlide = () => {
        setCurrentSlide((current) =>
            current === photos.length - 1 ? 0 : current + 1
        );
    };

    return (
        <div className="relative w-full h-52 overflow-hidden">
            {photos.map((photo, index) => (
                <div
                    key={index}
                    className={`absolute inset-0 w-full transition-opacity duration-500 ${
                        index === currentSlide ? "opacity-100" : "opacity-0"
                    }`}
                >
                    <img
                        src={photo}
                        alt="Photo"
                        className="mx-auto h-full w-auto object-contain"
                    />
                </div>
            ))}

            <div className="absolute inset-0 flex items-center justify-between px-5">
                <button
                    onClick={prevSlide}
                    className="btn btn-circle bg-base-200/75"
                >
                    ❮
                </button>

                <button
                    onClick={nextSlide}
                    className="btn btn-circle bg-base-200/75"
                >
                    ❯
                </button>
            </div>
        </div>
    );
}

export default PhotosSlideShow;