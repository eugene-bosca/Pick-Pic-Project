import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BackendProvider } from './context.tsx';
import './index.css';
import App from './App.tsx';

// Define your backend URL
//const backendUrl = 'http://localhost:8000'
const backendUrl = 'https://pick-pic-service-627889116714.northamerica-northeast2.run.app';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BackendProvider url={backendUrl}>
      <App />
    </BackendProvider>
  </StrictMode>
);