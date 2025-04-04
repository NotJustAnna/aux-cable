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

export const SuccessButton = classed.button`
    bg-green-500 hover:bg-green-600 text-white py-2 px-4 rounded-md
`;