import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/homePage";
import EventPage from "./pages/eventPage";
import ImageViewer from "./pages/imagePage";

function App() {
	return (
		<Router>
			<Routes>
			  <Route path="/" element={<HomePage />} />
				<Route path="/event/:eventId/" element={<EventPage />} />
				<Route path="/event/:eventId/image/:imageId/" element={<ImageViewer />} />
			</Routes>
		</Router>
	);
}

export default App;
