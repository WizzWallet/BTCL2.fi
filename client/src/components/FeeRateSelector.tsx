import { useDispatch } from 'react-redux';
import { RootDispatch } from '@/store';
import { useFees } from '@/hooks';
import { InputNumber } from 'antd';
import { Fees } from '@/service/mempool';
import { feeRateLabelMap, FeeRateResponse } from '@/types/bitcoin/types';

type KeysOfType = keyof Fees;
type FeeRateSelectorProps = {
  min?: number;
  feesoptions?: KeysOfType[];
  disabled?: boolean;
};

type MergedHTMLAttributes = Omit<React.HTMLAttributes<HTMLElement> & React.AnchorHTMLAttributes<HTMLElement>, 'type'>;

const FeeRateSelector = (props: MergedHTMLAttributes & FeeRateSelectorProps & {
  bordered?: boolean;
  small?: boolean
}) => {
  const { feesoptions, min, bordered, className, disabled } = props;
  const { fees: feeRates, level, customFee: customFeeRate } = useFees();
  const dispatch = useDispatch<RootDispatch>();
  const handleSelectFeeRate = (key: string) => {
    if (disabled) return;
    dispatch.global.save({ feeLevel: key });
  };
  // let customTips: string | undefined;
  // if (customFeeRate) {
  //   if (customFeeRate >= feeRates.fastestFee) {
  //     const symbol = customFeeRate === feeRates.fastestFee ? '~' : '<';
  //     customTips = `${symbol} ${feeRateLabelMap['fastestFee'].text}`;
  //   } else if (customFeeRate >= feeRates.halfHourFee) {
  //     const symbol = customFeeRate === feeRates.halfHourFee ? '~' : '<';
  //     customTips = `${symbol} ${feeRateLabelMap['halfHourFee'].text}`;
  //   } else if (customFeeRate >= feeRates.hourFee) {
  //     const symbol = customFeeRate === feeRates.hourFee ? '~' : '<';
  //     customTips = `${symbol} ${feeRateLabelMap['hourFee'].text}`;
  //   } else {
  //     customTips = `~ ${feeRateLabelMap['economyFee'].text}`;
  //   }
  // }
  let entries = Object.entries(feeRateLabelMap);
  if (feesoptions) {
    entries = feesoptions.map((key) => {
      return entries.find(([k]) => k === key) as any;
    }).filter((v) => !!v);
  } else {
    entries = ['fastestFee', 'halfHourFee', 'hourFee'].map((key) => {
      return entries.find(([k]) => k === key) as any;
    }).filter((v) => !!v);
  }
  return (
    <div className={`w-full grid-cols-4 grid grid-cols-card ${props.small ? 'grid-cols-card-mini' : ''} gap-2.5 ${className || ''}`}>
      {entries.map(([key, value]) => {
        return (
          <div
            key={key}
            className={`flex flex-col justify-center px-2 py-2 cursor-pointer rounded-md border-solid border-2 ${
              level == key ? 'border-primary' : bordered ? 'border-color' : 'border-transparent'
            }`}
            onClick={() => handleSelectFeeRate(key)}
          >
            <div className="text-base font-bold primary-text">{value.label}</div>
            <div className="text-primary">
              {feeRates[key as keyof FeeRateResponse]}
              <span className="text-xs secondary-text"> sat/vB</span>
            </div>
            {/* <div className="text-xs secondary-text">~ {value.text}</div> */}
          </div>
        );
      })}
      <div
        className={`flex flex-col justify-center px-2 py-2 cursor-pointer rounded-md border-solid border-2 ${
          level == 'custom' ? 'border-primary' : bordered ? 'border-color' : 'border-transparent'
        }`}
        onClick={() => handleSelectFeeRate('custom')}
      >
        <div className="text-base font-bold primary-text">Custom</div>
        <div className="flex items-center">
          <InputNumber
            size={'small'}
            value={customFeeRate}
            placeholder={props.small ? 'sat/vB' : undefined}
            min={min || feeRates.minimumFee}
            onChange={(rate) => {
              if (rate == null) {
                dispatch.global.save({ customFee: undefined });
                return;
              }
              dispatch.global.save({ customFee: rate });
            }}
            className="flex-1 w-0"
          />
          {props.small ? null : <span className="text-xs secondary-text ml-1"> sat/vB</span>}
        </div>
        {/* <div
          className={`text-xs secondary-text mt-1 ${customTips ? '' : 'invisible'}`}>{customTips ? customTips : 'qwert'}</div> */}
      </div>
    </div>
  );
};
export default FeeRateSelector;
