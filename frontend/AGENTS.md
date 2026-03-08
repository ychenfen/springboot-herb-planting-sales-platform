# Agent instructions (scope: frontend/ and subdirectories)

## Scope and layout
- Key directories:
  - `src/` (views, components, stores, router, api, utils)
  - `public/` (static assets)
  - `vite.config.js` (dev server and proxy)

## Commands
- Install: `npm install`
- Dev: `npm run dev` (default port 5173)
- Build: `npm run build`
- Preview: `npm run preview`

## API wiring
- Axios base URL is `/api` in `src/utils/request.js`.
- Dev proxy is in `vite.config.js` -> `http://localhost:8080`.
- If backend port or context path changes, update both files and the docs.

## Windows notes
- Use Node.js LTS.
- If port 5173 is unavailable, update `vite.config.js` and note it in docs.

## Do not
- Store DB connection info in the frontend.
