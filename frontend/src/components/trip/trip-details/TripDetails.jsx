import TripActivity from "../trip-activity/TripActivity.jsx";

function TripDetails({tripDetail: {tripName, startDate, endDate}, activities}) {
    return (
        <div className="w-[400px] h-[500px] overflow-auto p-4 border rounded-xl shadow-md bg-my-color">
            <h2 className="text-xl text-gray-500 font-semibold">{tripName}</h2>
            <p className="text-sm text-gray-500">{startDate} - {endDate}</p>
            <div className="recommendation-container mt-4">
                <div className="flex flex-col gap-3">
                    {activities.map((activity) => (
                        <TripActivity key={activity.placeDTO.name} tripActivity={activity}/>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default TripDetails;