import classed from "classed-components";

export const Card = classed.div`
    max-w-sm rounded-md shadow-lg bg-slate-800 p-6
`;

export const Title = classed.h1`
    text-2xl font-bold text-center
`;

export const Separator = classed.hr`
    my-3 border-slate-700
`;

export const MonospacedInput = classed.input`
    bg-slate-700 text-white border border-slate-600 rounded-md py-2 px-3 font-mono
`;

export const Centered = classed.div`
flex flex-column justify-center
`;

export const LinkButton = classed.button`
    text-indigo-400 hover:text-indigo-600 block text-center underline italic
`;

interface ButtonProps {
    size?: 'sm' | 'md' | 'lg';
}

const ButtonSizes: Record<ButtonProps['size'] & string, string> = {
    sm: 'text-sm rounded-sm py-1 px-2',
    md: 'rounded-md py-2 px-4',
    lg: 'text-lg rounded-lg py-2 px-4',
}

export const SuccessButton = classed.button<ButtonProps>`
    bg-green-500 hover:bg-green-600 text-white
    ${({ size }: ButtonProps) => ButtonSizes[size ?? 'md']}
`;

export const DangerButton = classed.button<ButtonProps>`
    bg-rose-500 hover:bg-rose-600 text-white
    ${({ size }: ButtonProps) => ButtonSizes[size ?? 'md']}
`;

export const MessageCorner = classed.div`
    absolute bottom-0 left-0 p-4 gap-2 flex flex-col
`;

export const InfoMessage = classed.div`
    bg-sky-700 py-2 px-3 rounded
`;

export const WarningMessage = classed.div`
    bg-yellow-600 py-2 px-3 rounded
`;

export const ErrorMessage = classed.div`
    bg-rose-700 py-2 px-3 rounded
`;

export const SuccessMessage = classed.div`
    bg-green-700 py-2 px-3 rounded
`;

// <div className="bg-slate-700 text-white flex flex-row mb-3">
//             <button className={`${showAccounts && 'bg-slate-600' || ''} flex-1 border rounded-s-md border-slate-600`}>Accounts</button>
//             <button className={`${!showAccounts && 'bg-slate-600' || ''} flex-1 border rounded-e-md border-slate-600`}>Token</button>
//         </div>

export const ButtonGroup = classed.div`
    bg-slate-700 text-white flex flex-row mb-3
`;

export const GroupButton = classed.button`
    flex-1 border border-slate-600 hover:bg-slate-500
    ${({ side }: { side: 'left' | 'right' }) => side === 'left' && 'rounded-s-md' || 'rounded-e-md'}
    ${({ active }: { active: boolean }) => active && 'bg-slate-600'}
`;