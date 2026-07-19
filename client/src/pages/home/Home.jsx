import { useEffect, useState } from "react";
import "./Home.css"


export default function Home(){
    const [pdfs, setPdfs] = useState([]);
    const [ordenacao, setOrdenacao] = useState("az")

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
        event.preventDefault();
        const arquivoSelecionado = document.getElementById("upload-file").files[0];

        if(!arquivoSelecionado) return;

        const formData = new FormData();
        formData.append("file", arquivoSelecionado);

        fetch("http://localhost:8080/pdfs", {
            method: "POST",
            body: formData
        })
        .then(r => r.json())
        .then(data => buscarPdfs(), document.getElementById("upload-file").value = "")
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

    const sortPdfs = () => {
        const copiaPdfs = [...pdfs]
        switch(ordenacao){
            case "az": return copiaPdfs.sort((a, b) => a.name.localeCompare(b.name));
            case "za": return copiaPdfs.sort((a, b) => b.name.localeCompare(a.name));
            case "pag-cres": return copiaPdfs.sort((a, b) => a.pages - b.pages);
            case "pag-decres": return copiaPdfs.sort((a, b) => b.pages - a.pages);
            default: return copiaPdfs;
        }
    }

    const pdfsOrdenados = sortPdfs();
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
                    <select value={ordenacao} onChange={(e) => setOrdenacao(e.target.value)}>
                        <option value="az">Ordem alfabética A-Z</option>
                        <option value="za">Ordem alfabética Z-A</option>
                        <option value="pag-cres">Ordem crescente de páginas</option>
                        <option value="pag-decres">Ordem decrescente de páginas</option>
                    </select>

                    <ul>
                        {pdfsOrdenados.map((pdf, idx) => (
                            <li className="pdf-box" key={idx}>
                                <div className="pdf-buttons">
                                    <button onClick={() => renameFile(pdf.name)}>renomear</button>
                                    <button onClick={() => handleFileDelete(pdf.name)}>deletar</button>
                                </div>
                                <img src="../../pdf-placeholder.png" />
                                <a href={`http://localhost:8080${pdf.url}`}>{pdf.name}</a>
                                <p>{pdf.pages} págs.</p>
                            </li>
                        ))}
                    </ul>
                </div>
                )}
                <div className="upload-file-form">
                    <h2>Enviar arquivo com extensão .pdf</h2>
                    <form onSubmit={handleFileUpload}>
                        <input type="file" accept=".pdf" id="upload-file"/>
                        <button type="submit">Enviar</button>
                    </form>
                </div>
            </div>
        </main>
    );
}