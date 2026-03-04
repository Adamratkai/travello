import {useState} from "react";

function PhotosSlideShow({photos}) {
    const [currentSlide, setCurrentSlide] = useState(0);

    const prevSlide = () => {
        setCurrentSlide((currentSlide) =>
            currentSlide === 0 ? photos.length - 1 : currentSlide - 1
        );
    };

    const nextSlide = () => {
        setCurrentSlide((currentSlide) =>
            currentSlide === photos.length - 1 ? 0 : currentSlide + 1
        );
    };

    return (
        <div className="relative w-full h-52 overflow-hidden">
            {photos.map((photo, index) => (
                <div
                    key={photo + index}
                    className={`absolute inset-0 w-full transition-opacity duration-500 ${
                        index === currentSlide ? "opacity-100" : "opacity-0"
                    }`}
                >
                    <img
                        src={`/api/location/photos/${photo}`}
                        alt="Photo"
                        className="mx-auto h-full w-auto object-contain"
                    />
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
            ))}
        </div>
    );
}

export default PhotosSlideShow;
