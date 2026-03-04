import {useEffect, useRef, useState} from "react";
import {googlePlaceTypes} from "../../../../resources/googlePlaceTypes.js";

function formatPlaceType(placeType) {
    return placeType.replace(/_/g, " ");
}

function PlaceTypeSelector({onSelect}) {
    const [query, setQuery] = useState("restaurant");
    const [filteredOptions, setFilteredOptions] = useState([]);
    const inputRef = useRef(null);

    useEffect(() => {
        if (onSelect) {
            onSelect("restaurant");
        }
    }, [onSelect]);

    const handleChange = (event) => {
        const value = event.target.value;
        setQuery(value);
        if (value.trim() === "") {
            setFilteredOptions([]);
            return;
        }
        const results = googlePlaceTypes
            .filter((type) => type.toLowerCase().includes(value.toLowerCase()))
            .slice(0, 10);
        setFilteredOptions(results);
    };

    const handleSelect = (type) => {
        setQuery(formatPlaceType(type));
        setFilteredOptions([]);
        onSelect(type);
    };

    const handleBlur = (event) => {
        if (!inputRef.current.contains(event.relatedTarget)) {
            setFilteredOptions([]);
        }
    };

    return (
        <div className="relative w-full max-w-md" ref={inputRef}>
            <input
                type="text"
                value={query}
                onChange={handleChange}
                onBlur={handleBlur}
                placeholder="Search place type..."
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-black"
                style={{
                    width: "100%",
                    padding: "10px",
                    fontSize: "16px",
                    border: "1px solid white",
                    borderRadius: "5px",
                    backgroundColor: "#3E9DE151"
                }}
            />
            {filteredOptions.length > 0 && (
                <ul className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-md max-h-40 overflow-auto">
                    {filteredOptions.map((type) => (
                        <li
                            key={type}
                            onMouseDown={(e) => e.preventDefault()}
                            onClick={() => handleSelect(type)}
                            className="p-2 cursor-pointer hover:bg-gray-200 text-black"
                        >
                            {formatPlaceType(type)}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default PlaceTypeSelector;
