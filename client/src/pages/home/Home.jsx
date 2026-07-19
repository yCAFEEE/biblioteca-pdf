import { useEffect, useState } from "react";
import "./Home.css"


export default function Home(){
    const [pdfs, setPdfs] = useState([]);

    const buscarPdfs = () => {
        fetch("http://localhost:8080/pdfs")
        .then(r => r.json())
        .then(setPdfs)
        .catch(err => console.error("Falha ao buscar PDFs", err));
    };

    useEffect(() => {
        buscarPdfs();
    }, []);

    const handleFileUpload = (event) => {
        const arquivoSelecionado = event.target.files[0];

        if(!arquivoSelecionado) return;

        const formData = new FormData();
        formData.append("file", arquivoSelecionado);

        fetch("http://localhost:8080/pdfs", {
            method: "POST",
            body: formData
        })
        .then(r => r.json())
        .then(data => buscarPdfs())
        .catch(err => console.error("Falha ao enviar arquivo ", err));
    };

    const handleFileDelete = (nomeArquivo) => {
        fetch(`http://localhost:8080/pdfs/${nomeArquivo}`, {
            method: "DELETE"
        })
        .then(r => r.json())
        .then(data => buscarPdfs())
        .catch(err => console.error("Erro ao deletar arquivo", err));
    };

    const renameFile = (nomeAntigo) => {
        let novoNome = prompt(`Digite o novo nome para ${nomeAntigo}: `);

        fetch(`http://localhost:8080/pdfs/${nomeAntigo}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ newName: novoNome })
        })
        .then(r => r.json())
        .then(data => buscarPdfs())
        .catch(err => console.error("Erro ao renomear arquivo"));
    };

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
                                <div className="pdf-buttons">
                                    <button onClick={() => renameFile(pdf.name)}>renomear</button>
                                    <button onClick={() => handleFileDelete(pdf.name)}>deletar</button>
                                </div>
                                <img src="../../pdf-placeholder.png" />
                                <a href={`http://localhost:8080${pdf.url}`}>{pdf.name}</a>
                            </li>
                        ))}
                    </ul>
                </div>
                )}
                <input type="file" accept=".pdf" onChange={handleFileUpload} id="upload-button"/>
            </div>
        </main>
    );
}