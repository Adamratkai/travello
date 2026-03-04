const MAX_RATING = 5;
const RecommendedPlace = ({name, price, rating, onPlaceClick}) => {
    return (
        <div className="card lg:card-side bg-base-200 shadow-sm w-[200px]  ">
            <div className="card-body">
                <h2 className="card-title justify-center">{name}</h2>
                <p>Price: {"$".repeat(Math.max(1, Math.floor(price)))}</p>
                <div className="rating">
                    {[...Array(MAX_RATING)].map((_, index) => (
                        <div
                            key={index}
                            className="mask mask-star"
                            aria-label={`${index + 1} star`}
                            aria-current={index + 1 === Math.round(rating) ? "true" : undefined}
                        ></div>
                    ))}
                    {rating}
                </div>
                <div className="card-actions justify-end">
                    <button className="btn btn-primary" onClick={onPlaceClick}>Details</button>
                </div>
            </div>
        </div>
    );
};

export default RecommendedPlace;