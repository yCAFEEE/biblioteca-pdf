import { BrowserRouter as Router, useLocation } from "react-router-dom";
import AppRoutes from "./routes/AppRoutes.jsx";

import './style.css';

function App() {
  return (
    <Router>
      <AppRoutes />
    </Router>
  );
}

export default App
