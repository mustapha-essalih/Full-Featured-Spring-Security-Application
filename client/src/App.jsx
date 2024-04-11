import { Route, Routes, Navigate } from "react-router-dom";
import Signup from "./components/Singup";
import Login from "./components/Login";
import Home from "./components/Main/Home";

function App() {
	const user = localStorage.getItem("token");

	return (
		<Routes>
			{/* {user && <Route path="/home" exact element={<Home />} />} */}
			<Route path="/home" exact element={<Home />} />
			<Route path="/signup" exact element={<Signup />} />
			<Route path="/login" exact element={<Login />} />
			<Route path="/" element={<Navigate replace to="/login" />} />
		</Routes>
	);
}

export default App;
