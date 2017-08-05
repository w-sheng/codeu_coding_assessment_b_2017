// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;
import com.google.codeu.mathlang.core.tokens.StringToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;
import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {

	private StringBuilder token;
	private String source;
	private int at;
	private ArrayList<String> symbols = new ArrayList<String>(Arrays.asList(";", "+", "-", "="));

	public MyTokenReader(String source) {
		// Your token reader will only be given a string for input. The string will
		// contain the whole source (0 or more lines).
		this.token = new StringBuilder();
		this.source = source;
		this.at = 0;
	}

	@Override
	public Token next() throws IOException {

		// skip leading whitespaces
		while (remaining() > 0 && Character.isWhitespace(peek())) {
			// ignore result if whitespace
			read();
		}
		if (remaining() <= 0) {
			return null;
		} else if (peek() == '"') {
			return readWithQuotes(); // create a StringToken
		} else {
			return readWithNoQuotes(); // create all other types of Tokens
		}
	}

	private int remaining() {
		return source.length() - at;
	}

	private char peek() throws IOException {
		if (at < source.length()) {
			return source.charAt(at);
		} else {
			throw new IndexOutOfBoundsException("This is outside of the source string.");
		}
	}	

	private char read() throws IOException {
		final char c = peek();
		at += 1;
		return c;
	}	

	private Token readWithNoQuotes() throws IOException {
		token.setLength(0); // clear the token

		int start = at; // for reference
		while (remaining() > 0 && !Character.isWhitespace(peek())) {
			token.append(read());
		}
		String tokenString = token.toString();

		if (tokenString.length() > 1 && tokenString.endsWith(";")) {
			tokenString = tokenString.substring(0, tokenString.length() - 1);
			at -= 1;
		} 

		String firstchar = Character.toString(tokenString.charAt(0));
		
		if (symbols.contains(firstchar)) { // create SymbolToken
			
			at = start + 1; // update at val to char after nameToken
			return new SymbolToken(firstchar.charAt(0));
		
		} else 	if (isInteger(firstchar)) {	// create NumberToken
			
			at = start + 1; // update at val to char after nameToken
			return new NumberToken(Double.parseDouble(firstchar));

		} else { // create NameToken
			
			int i = 0;
			String ch = Character.toString(tokenString.charAt(i));

			String nameToken = ch;
			while (!symbols.contains(ch) && !isInteger(ch) && i < tokenString.length() - 1) {
				i++;
				ch = Character.toString(tokenString.charAt(i));
				nameToken += ch;
			}

			// make sure NameToken does not include any other tokens in it
			String lastChar = Character.toString(nameToken.charAt(nameToken.length() - 1));
			if (symbols.contains(lastChar) || isInteger(lastChar)) {
				nameToken = nameToken.substring(0, nameToken.length() - 1);
			}

			at = start + nameToken.length(); // update at val to char after nameToken			
			return new NameToken(nameToken);
		}
	}

	private Token readWithQuotes() throws IOException {
		token.setLength(0); // clear the token
		if (read() != '"') {
			throw new IOException("Strings must start with opening quotes!");
		}
		while(peek() != '"') {
			token.append(read());
		}
		read(); // read closing quote that allows us to exit the loop
		return new StringToken(token.toString());
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
