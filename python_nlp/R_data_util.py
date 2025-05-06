
keyword_data = 'data'
keyword_flag = 'flag'

class R_dataType:
    flag = False
    data = None


    def __init__(self,flag=False,data=None):
        super(R_dataType,self).__init__()
        self.flag = flag
        self.data = data

    def get_in_data(self,keyword):
        if keyword in self.data:
            return self.data[keyword]
        else:
            return None

    def __dict__(self):
        return {
            'flag': self.flag,
            'data': self.data,
        }