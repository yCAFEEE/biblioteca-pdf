import express from 'express';
import path from 'path';
import fs from 'fs/promises';
import cors from 'cors';
import { fileURLToPath } from 'url';

const app = express();
const PORT = process.env.PORT || 3000;

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const pdfsDir = path.resolve(__dirname, '..', 'client', 'public', 'pdfDir');

app.use(cors({
		origin: [
			process.env.FRONTEND_URL,
			"http://localhost:5173", 
			"http://127.0.0.1:5173"
		],
		methods: ["GET", "POST"],
		allowedHeaders: ['Content-Type', 'Authorization']
	})
);

app.use("/pdfs-files", express.static(pdfsDir));

app.get("/pdfs", async (request, response) => {
    try{
        const files = await fs.readdir(pdfsDir);
        const pdfFiles = files.filter(f => path.extname(f).toLocaleLowerCase() === ".pdf");
        const list = pdfFiles.map(name => ({
            name,
            url: `/pdfs-files/${encodeURIComponent(name)}`
        }));
        response.json(list);
    } catch (err) {
        console.error("Erro: ", err);
        response.status(500).json({ error: 'Erro ao listar os arquivos PDFs' })
    }
});


app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});