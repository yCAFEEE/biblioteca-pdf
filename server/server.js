import express from 'express';
import path from 'path';
import { fileToUrlToPath } from 'url';

const app = express();
const PORT = process.env.PORT || 3000;

const __filename = fileToUrlToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const pdfsDir = path.resolve(__dirname, 'public', 'pdfDir');

app.get("/pdfs", (request, response) => {

});


app.listen(PORT);