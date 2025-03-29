import { useParams } from "react-router-dom";
import ImageViewer from "../component/imageViewer";
import { useBackendContext } from "../context";
import { useState, useEffect } from 'react';

import "./eventPage.css";

type Params = {
	eventId: string;
};

type Event = {
    event_id: string;
    event_name : string;
    owner : string;
    last_modified: Date;
}

type EventContent = {
	image_id: string;
	owner: string;
	file_name: string;
	score: number;
}

const EventPage = () => {
	const { eventId } = useParams<Params>();

	const { backendUrl } = useBackendContext();

	const [ event, setEvent ] = useState<Event | undefined>();

	const [ eventContents, setEventContents ] = useState<EventContent[] | undefined>();

	useEffect(() => {

		const fetchEvent = async () => {
			try {
			  const response = await fetch(`${backendUrl}/event/${eventId}`, {
				method: "GET",
				headers: {
					"Content-Type": "application/json",
				},
			});
			  const result = await response.json();
			  setEvent(result);
			} catch (error) {
			  console.error("Error fetching data:", error);
			}
		  };

		  const fetchEventContent = async () => {
			try {
				const response = await fetch(`${backendUrl}/event/${eventId}/content/`, {
					method: "GET",
					headers: {
						"Content-Type": "application/json",
					},
				});
				const result = await response.json();
				setEventContents(result);
			} catch (error) {
				console.error("Error fetching data:", error);
			}
		  }


	  
		  fetchEvent();
		  fetchEventContent();
	  
	}, []);

	return (
		<div className="event-page">
			<h1 className="event-name">{event ? event.event_name : "Unknown Event"}</h1>
			<div className="image-gallery">
				{eventContents?.map((eventContent, _) => (
					<ImageViewer eventId={eventId ?? ""} imageId={eventContent.image_id} />
				))}
			</div>
		</div>
	);
};

export default EventPage;
