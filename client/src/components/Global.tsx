
import { useFeesWithRefresh } from '@/hooks';
import { App as AntdApp, ConfigProvider, theme } from 'antd';


type GlobalProps = {
  children: React.ReactNode;
}
const Global:React.FC<GlobalProps> = (props) => {
  useFeesWithRefresh()
  return (
    <ConfigProvider theme={{
      algorithm: theme.darkAlgorithm,
      token: {
        colorPrimary: '#EBC28E',
      },
      components: {
        Button: {
          primaryColor: '#0b0b0f',
          contentFontSize: 18,
          paddingBlock: 10,
        }
      }
    }}>
      <AntdApp>
        {props.children}
      </AntdApp>
    </ConfigProvider>
  )
}

export default Global;
