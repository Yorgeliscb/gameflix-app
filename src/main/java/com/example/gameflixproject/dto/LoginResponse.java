package com.example.gameflixproject.dto;

public record LoginResponse(String token, String tokenType, long expiresIn) {}
