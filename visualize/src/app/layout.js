import './globals.css'
import React from "react";
import CssBaseline from "@mui/material/CssBaseline";
import ThemeProvider from "@mui/material/styles/ThemeProvider";
import {Inter} from 'next/font/google'
import {AppRouterCacheProvider} from "@mui/material-nextjs/v13-appRouter";
import {theme} from "@/app/theme";
import Toolbar from "@mui/material/Toolbar";
import {Box} from "@mui/material";
import AppBar from "@mui/material/AppBar";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Create Next App',
  description: 'Generated by create next app',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AppRouterCacheProvider>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <AppBar position="static" color="transparent">
              <Toolbar>
                <Box sx={{
                  flexGrow: 1,
                  display: "flex",
                  alignContent: "center",
                  alignItems: "center",
                  flexDirection: "column",
                  }}>
                  <Typography variant="h4">Confluence Blog Post Statistics</Typography>
                </Box>
              </Toolbar>
            </AppBar>
            <Container
              data-testid="layout"
              maxWidth="lg"
              sx={{ marginTop: 2, marginBottom: 2 }}
            >
            {children}
            </Container>
          </ThemeProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  )
}
