import { useEffect, useState } from "react";


export default function Home(){
    const [pdfs, setPdfs] = useState([]);

    useEffect(() => {
        const pdfFiles = [
            "../../pdfDir/IRODORI A1 Introdutório.pdf",
            "../../pdfDir/Bass Solo Segredos da Improvisação - Nico Assumpção.pdf",
            "../../pdfDir/Sistemas Operacionais Modernos (Andrew S. Tanenbaum, Herbert Bos).pdf"
        ];

        setPdfs(pdfFiles);
    });

    return(
        <main>
            <div className="content-container">
                <ul>
                    {pdfs.map((pdf, idx) => (
                        <li key={idx}>
                            <a href={`/pdfs/${pdf}`}>{pdf}</a>
                        </li>
                    ))}
                </ul>
            </div>
        </main>
    );
}