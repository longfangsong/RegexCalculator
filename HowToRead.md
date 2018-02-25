# 源码提纲

1. `RegexPart`接口

  这是所有正则表达式中的元素都要实现的一个接口。

  这里有`or`（|）、`concat`（·）和`repeat`（\*）三大运算符的实际实现，值得注意的是`concat`在面对

  - 两个`RegexPartOptioned`拼接
  - `RegexPartOptioned`和非`RegexPartOptioned`拼接

  时都运用了分配律，这是为了方便转正规文法时的处理。

  值得一题的是，有一个叫`SubstitutableRegexPart`的接口继承了这个接口，这个接口中的`substitute(generator:Generator)`的在将正规文法转换到正则表达式时使用。

2. `TerminalChar`

    终结符，即正则表达式中的一般字符。

3. `NonTerminalChar`

    非终结符，一般用于表示一个正则表达式的一部分。

4. `RegexPartOptioned` & `RegexPartConcated` & `RegexPartRepeated`

    这三个类分别代表了某些RegexPart在经过了`or`（|）、`concat`（·）和`repeat`（\*）运算之后的结果。

    其中`RegexPartOptioned`与其他`RegexPart`进行`or`运算的结果仍然是“单层”的`RegexPartOptioned`，即不会出现某个`RegexPartOptioned`中包含的`RegexPart`也是一个`RegexPartOptioned`的情况。

    `RegexPartConcated`亦然，不会出现`RegexPartConcated`中套`RegexPartConcated`的情况。

    相似地，由于`RegexPartRepeated`的幂等性质，对其调用`repeat`会返回其本身。

    这些特性也是为了方便转正规文法时的处理。

5. `Generator`

    产生式，即形如 A->abc 这样的式子。

    其中`regulized`是将正则表达式转为正规文法的关键。

6. `Grammar`

    正规文法。

