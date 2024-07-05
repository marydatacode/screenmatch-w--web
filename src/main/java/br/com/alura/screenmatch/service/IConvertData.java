package br.com.alura.screenmatch.service;

public interface IConvertData {
    <S> S getData(String json, Class<S> classe);
}
