library(readxl)
Data1<- read_excel("Documents/Recerca/DadesTest1.xlsx")
res1 <- t.test(score ~ group, data = DadesTest1, paired = TRUE)
res1

Data2 <- read_excel("Documents/Recerca/DadesTest2v2.xlsx")
res2 <- t.test(score ~ group, data = DadesTest2v2, paired = TRUE)
res2

