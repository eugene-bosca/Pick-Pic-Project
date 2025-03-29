import React, { useEffect, useState } from "react";
import { useBackendContext } from "../context";

import "./imageViewer.css";

interface ImageViewerProps {
	eventId: string;
	imageId: string;
	width?: number;
	height?: number;
}

const ImageViewer: React.FC<ImageViewerProps> = ({
	eventId,
	imageId,
	width = 300,
	height = 300,
}) => {

	const [ image, setImage ] = useState<string>();

	const { backendUrl } = useBackendContext();	

	useEffect(() => {

		const fetchImage = async (imageId:string) => {
			try {
				const response = await fetch(`${backendUrl}/event/${eventId}/image/${imageId}/`, {
					method: "GET",
					headers: {
						"Content-Type": "application/json",
					},
				});
				const blob = await response.blob();
				const objectURL = URL.createObjectURL(blob);
				setImage(objectURL);
			} catch (error) {
				console.error("Error fetching data:", error);
			}
		  }

		  fetchImage(imageId);

	}, []);


	return (
		<div className="image-viewer" style={{ width, height }}>
			{ image ? <img src={image} alt={`Event Image`} /> : <p>Loading...</p> }
		</div>
	);
};

export default ImageViewer;
