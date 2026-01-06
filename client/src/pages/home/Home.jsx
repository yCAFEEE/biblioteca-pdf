import { useEffect, useState } from "react";


export default function Home(){
    const [pdfs, setPdfs] = useState([]);

    useEffect(() => {
        fetch("http://localhost:3000/pdfs")
        .then(r => r.json())
        .then(setPdfs)
        .catch(err => {
            console.error("Falha ao buscar PDFs", err);
        });
    }, []);

    return(
        <main>
            <div className="content-container">
                <h1>PDFs</h1>
                {pdfs.length === 0 ? (
                    <p>Nenhum arquivo PDF encontrado no caminho: ./client/public/pdfDir/</p>
                ) : (
                <ul>
                    {pdfs.map((pdf, idx) => (
                        <li key={idx}>
                            <a href={`http://localhost:3000${pdf.url}`}>{pdf.name}</a>
                        </li>
                    ))}
                </ul>
                )}
            </div>
        </main>
    );
}