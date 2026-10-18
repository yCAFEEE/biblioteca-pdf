import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const API = "http://localhost:8080/"

export default function Folder(){
    const { folderName } = useParams();

    const [pdfs, setPdfs] = useState([]);
    const [ordenacao, setOrdenacao] = useState("az");
    const [busca, setBusca] = useState("");

    const buscarPdfs = () => {
        fetch(`${API}folders/${encodeURIComponent(folderName)}/pdfs`)
        .then(r => r.json())
        .then(setPdfs)
        .catch(err => console.error("Falha ao buscar PDFs", err));
    };

    useEffect(() => {
        buscarPdfs();
    }, []);

    const handleFileDelete = (nomeArquivo) => {
        fetch(`${API}folders/${folderName}/pdfs/${nomeArquivo}`, {
            method: "DELETE"
        })
        .then(r => r.json())
        .then(data => buscarPdfs())
        .catch(err => console.error("Erro ao deletar arquivo", err));
    };

    const sortedPdfs = pdfs.filter((pdf) => pdf.name.toLowerCase().includes(busca.toLowerCase())).sort((a, b) => {
        switch(ordenacao){
            case "az": return a.name.localeCompare(b.name);
            case "za": return b.name.localeCompare(a.name);
            case "pag-cres": return Number(a.pages) - Number(b.pages);
            case "pag-decres": return Number(b.pages) - Number(a.pages);
            default: return 0;
        }
    });

    return(
        <>
            <main>
                <div className="content-container">
                    <div className="page-title-containter">
                        <h1>Biblioteca PDF</h1>
                        <h2>{folderName}</h2>
                        <p>Arquivos presentes na pasta <strong>{folderName}</strong></p>
                    </div>
                    {pdfs.length === 0 ? (
                        <div className="error-container">
                            <h2>Erro</h2>
                            <p>Erro no servidor ou nenhum arquivo PDF encontrado no caminho</p>
                        </div>
                    ) : (
                    <div className="pdfs-container">
                        <div className="filter-container">
                            <select value={ordenacao} onChange={(e) => setOrdenacao(e.target.value)}>
                                <option value="az">Ordem alfabética A-Z</option>
                                <option value="za">Ordem alfabética Z-A</option>
                                <option value="pag-cres">Ordem crescente de páginas</option>
                                <option value="pag-decres">Ordem decrescente de páginas</option>
                            </select>
                            <input type="search" placeholder="Buscar arquivo..." value={busca} onChange={(e) => setBusca(e.target.value)}/>
                        </div>

                        {sortedPdfs.length === 0 ? (
                            <p>Nenhum arquivo encontrado com o nome "{busca}"</p>
                        ) : (
                            <ul>
                                {sortedPdfs.map((pdf, idx) => (
                                    <li className="pdf-box" key={idx}>
                                        <div className="pdf-buttons">
                                            <button onClick={() => handleFileDelete(pdf.name)} id="deletar-button">Deletar</button>
                                        </div>
                                        <img src={pdf.thumbnail ? pdf.thumbnail : "../../pdf-placeholder.png"} alt={`Capa de ${pdf.name}`} onError={(e) => {e.target.src = "../../pdf-placeholder.png"}} />
                                        <div className="pdf-info">
                                            <a href={`${API}${pdf.url}`}>{pdf.name}</a>
                                            <p>{pdf.pages} págs.</p>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                    )}
                    <a href="/">Voltar à página principal</a>
                </div>
            </main>
        </>
    );
}