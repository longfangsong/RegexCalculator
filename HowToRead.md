# 源码提纲

1. `regexParts.RegexPart`接口

  这是所有正则表达式中的元素都要实现的一个接口。

  这里有`or`（|）、`concat`（·）和`repeat`（\*）三大运算符的实际实现，值得注意的是`concat`在面对

  - 两个`regexParts.RegexPartOptioned`拼接
  - `regexParts.RegexPartOptioned`和非`regexParts.RegexPartOptioned`拼接

  时都运用了分配律，这是为了方便转正规文法时的处理。

  值得一题的是，有一个叫`regexParts.SubstitutableRegexPart`的接口继承了这个接口，这个接口中的`substitute(generator:grammar.Generator)`的在将正规文法转换到正则表达式时使用。

2. `regexParts.TerminalChar`

    终结符，即正则表达式中的一般字符。

3. `regexParts.NonTerminalChar`

    非终结符，一般用于表示一个正则表达式的一部分。

4. `regexParts.RegexPartOptioned` & `regexParts.RegexPartConcated` & `regexParts.RegexPartRepeated`

    这三个类分别代表了某些RegexPart在经过了`or`（|）、`concat`（·）和`repeat`（\*）运算之后的结果。

    其中`regexParts.RegexPartOptioned`与其他`regexParts.RegexPart`进行`or`运算的结果仍然是“单层”的`regexParts.RegexPartOptioned`，即不会出现某个`regexParts.RegexPartOptioned`中包含的`regexParts.RegexPart`也是一个`regexParts.RegexPartOptioned`的情况。

    `regexParts.RegexPartConcated`亦然，不会出现`regexParts.RegexPartConcated`中套`regexParts.RegexPartConcated`的情况。

    相似地，由于`regexParts.RegexPartRepeated`的幂等性质，对其调用`repeat`会返回其本身。

    这些特性也是为了方便转正规文法时的处理。

5. `grammar.Generator`

    产生式，即形如 A->abc 这样的式子。

    其中`regulized`是将正则表达式转为正规文法的关键。

6. `grammar.grammar`

    正规文法。

