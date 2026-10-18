## Funcionalidades/Features
### Configurações
- [ ] Botão de configurações
- [ ] Menu de configurações
- [ ] Opção de desativar/ativar o carregamento da primeira página para capa do arquivo e salvar essa escolha no navegador

### Arquivos e pastas
- [ ] Trocar botões de **"Renomear"** e **"Deletar"** por um botão multifunção **"..."** que, ao ser clicado, exibirá as funções **"Renomear"**, **"Deletar"** e **"Mover para pasta principal"** (caso o arquivo esteja dentro de uma pasta criada) em um menu suspenso ao lado do pdf-box.

### Arquivos e pastas (ideias mais complexas que serão implementadas no futuro)
- [ ] Função de clicar e arrastar um arquivo na pasta principal para uma pasta criada. (Drag and Drop)
- [ ] Dentro de uma pasta criada, um botão de **"+"** que, ao ser clicado, abrirá uma tela que mostra os arquivos na pasta principal. Dentro dessa tela, o usuário poderá selecionar arquivos da pasta principal para serem movidos para a pasta criada.
- [ ] Deve ser possível selecionar arquivos na aplicação. Caso os arquivos estejam na pasta principal, deve ser exibido a função de **"Mover para uma pasta"** que, ao ser clicada, mostre uma tela para selecionar uma pasta (ou mais de uma pasta) em que deseja mover os arquivos. Caso os arquivos estejam em uma pasta criada, deve ser exibido a função de **"Mover para a pasta principal"** que, ao ser clicada, moverá todos os arquivos selecionados para a pasta principal.

## UX
- [ ] Feedback visual de carregamento de arquivos e pastas (mostrar um **"carregando..."** enquanto o backend não dá um retorno)

## Refatoração
- [ ] Mover a pasta `pdfDir/` para `server/`