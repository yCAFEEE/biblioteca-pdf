import { useEffect, useState } from "react";
import "./Home.css"


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
                    <div className="error-container">
                        <h2>Erro</h2>
                        <p>Erro no servidor ou nenhum arquivo PDF encontrado no caminho: ./client/public/pdfDir/</p>
                    </div>
                ) : (

                <div className="pdfs-container">
                    <ul>
                        {pdfs.map((pdf, idx) => (
                            <li className="pdf-box" key={idx}>
                                <img src="../../pdf-placeholder.png" />
                                <a href={`http://localhost:3000${pdf.url}`}>{pdf.name}</a>
                            </li>
                        ))}
                    </ul>
                </div>
                )}
            </div>
        </main>
    );
}