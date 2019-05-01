## Things to check

1. Reader/InputStream

2. BufferedReader

3. what is the meaning of a private constructor with no input?

   -----------------------------------------------
   |           | 类内部 | package内 | 子类 | 其他 |
   | --------- | :----: | :-------: | :--: | :--: |
   | public    |   Y    |     Y     |  Y   |  Y   |
   | protected |   Y    |     Y     |  Y   |  N   |
   | default   |   Y    |     Y     |  N   |  N   |
   | private   |   Y    |     N     |  N   |  N   |

   

4. keyword: implements

```java
	public Symbol next_token() throws IOException {
        int yy_anchor = true;
        int yy_state = this.yy_state_dtrans[this.yy_lexical_state];
        int yy_next_state = true;
        int yy_last_accept_state = -1;
        boolean yy_initial = true;
        this.yy_mark_start();
        int yy_this_accept = this.yy_acpt[yy_state];
        if (0 != yy_this_accept) {
            yy_last_accept_state = yy_state;
            this.yy_mark_end();
        }

        while(true) {
            int yy_lookahead;
            if (yy_initial && this.yy_at_bol) {
                yy_lookahead = 128;
            } else {
                yy_lookahead = this.yy_advance();
            }

            yy_next_state = true;
            int yy_next_state = this.yy_nxt[this.yy_rmap[yy_state]][this.yy_cmap[yy_lookahead]];
            if (129 == yy_lookahead && yy_initial) {
                return new Symbol(0);
            }

            if (-1 != yy_next_state) {
                yy_state = yy_next_state;
                yy_initial = false;
                yy_this_accept = this.yy_acpt[yy_next_state];
                if (0 != yy_this_accept) {
                    yy_last_accept_state = yy_next_state;
                    this.yy_mark_end();
                }
            } else {
                if (-1 == yy_last_accept_state) {
                    throw new Error("Lexical Error: Unmatched Input.");
                }

                int yy_anchor = this.yy_acpt[yy_last_accept_state];
                if (0 != (2 & yy_anchor)) {
                    this.yy_move_end();
                }

                this.yy_to_mark();
                switch(yy_last_accept_state) {
                case -7:
                case -6:
                case -5:
                case -4:
                case -3:
                case -2:
                case -1:
                case 6:
                    break;
                case 0:
                default:
                    this.yy_error(0, false);
                    break;
                case 1:
                    int val = new Integer(this.yytext());
                    Symbol S = new Symbol(15, new IntLitTokenVal(this.yyline + 1, CharNum.num, val));
                    CharNum.num += this.yytext().length();
                    return S;
                case 2:
                    CharNum.num = 1;
                    break;
                case 3:
                    CharNum.num += this.yytext().length();
                    break;
                case 4:
                    Symbol S = new Symbol(28, new TokenVal(this.yyline + 1, CharNum.num));
                    ++CharNum.num;
                    return S;
                case 5:
                    ErrMsg.fatal(this.yyline + 1, CharNum.num, "ignoring illegal character: " + this.yytext());
                    ++CharNum.num;
                }

                yy_initial = true;
                yy_state = this.yy_state_dtrans[this.yy_lexical_state];
                yy_next_state = true;
                yy_last_accept_state = -1;
                this.yy_mark_start();
                yy_this_accept = this.yy_acpt[yy_state];
                if (0 != yy_this_accept) {
                    yy_last_accept_state = yy_state;
                    this.yy_mark_end();
                }
            }
        }
    }
```

