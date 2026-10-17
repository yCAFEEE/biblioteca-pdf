import { Routes, Route } from "react-router-dom";
import Home from "../pages/home/Home.jsx";
import Folder from "../pages/folder/Folder.jsx";

export default function AppRoutes(){
    return(
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/:folderName" element={<Folder />} />
        </Routes>
    );
}