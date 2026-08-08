ALTER TABLE sessao
    ADD CONSTRAINT uk_sessao_pauta UNIQUE (pauta_id);