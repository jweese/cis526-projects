from dataset import *

d = DataSet('/export/projects/grammar13/nucle', 1)
s = d.get(0)

for span in s.pos_spans('NN'):
  print span
