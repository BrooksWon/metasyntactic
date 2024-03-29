// Copyright 2008 Google Inc. All rights reserved.

package org.metasyntactic.automata.compiler.java.scanner.keywords;

public class CatchKeywordToken extends KeywordToken {
  public static final CatchKeywordToken instance = new CatchKeywordToken();

  private CatchKeywordToken() {
    super("catch");
  }

  protected Type getTokenType() {
    return Type.CatchKeyword;
  }
}
