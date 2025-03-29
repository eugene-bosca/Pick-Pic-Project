import React, { createContext, useContext, ReactNode } from 'react';

interface BackendContextType {
  backendUrl: string;
}

const BackendContext = createContext<BackendContextType | undefined>(undefined);

// Create a provider component
interface BackendProviderProps {
  children: ReactNode;
  url: string;
}

export const BackendProvider: React.FC<BackendProviderProps> = ({ children, url }) => {
  return (
    <BackendContext.Provider value={{ backendUrl: url }}>
      {children}
    </BackendContext.Provider>
  );
};

// Custom hook to use the context
export const useBackendContext = (): BackendContextType => {
  const context = useContext(BackendContext);
  if (!context) {
    throw new Error('useBackendContext must be used within a BackendProvider');
  }
  return context;
};